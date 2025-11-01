package com.soft.base.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysDictData;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.enums.QuartzIntervalEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysScheduleJobMapper;
import com.soft.base.model.dto.DictDataDto;
import com.soft.base.model.request.CreateJobRequest;
import com.soft.base.model.request.EditJobRequest;
import com.soft.base.model.request.GetQuartzTasksRequest;
import com.soft.base.model.vo.GetJobVo;
import com.soft.base.model.vo.GetQuartzTasksVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysDictDataService;
import com.soft.base.service.SysScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cyq
 * @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Service实现
 * @createDate 2025-07-11 19:56:32
 */
@Service
public class SysScheduleJobServiceImpl extends ServiceImpl<SysScheduleJobMapper, SysScheduleJob>
        implements SysScheduleJobService {

    private final SysScheduleJobMapper sysScheduleJobMapper;

    private final Scheduler scheduler;

    private final SysDictDataService sysDictDataService;

    @Autowired
    public SysScheduleJobServiceImpl(SysScheduleJobMapper sysScheduleJobMapper,
                                     Scheduler scheduler,
                                     SysDictDataService sysDictDataService) {
        this.sysScheduleJobMapper = sysScheduleJobMapper;
        this.scheduler = scheduler;
        this.sysDictDataService = sysDictDataService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createJob(CreateJobRequest request) {
        SysScheduleJob sysScheduleJob = new SysScheduleJob();
        BeanUtils.copyProperties(request, sysScheduleJob);
        sysScheduleJobMapper.insert(sysScheduleJob);
        configJob(sysScheduleJob);
    }

    @Override
    public PageVo<GetQuartzTasksVo> getQuartzTasks(GetQuartzTasksRequest request) {
        Page<GetQuartzTasksVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysScheduleJobMapper.getQuartzTasks(page, request);
        PageVo<GetQuartzTasksVo> pageVo = new PageVo<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());

        List<DictDataDto> sysDictDataList = sysDictDataService.getByDictType(5L);
        Map<String, String> stringStringMap = sysDictDataList.stream().collect(Collectors.toMap(DictDataDto::getValue, DictDataDto::getLabel));

        pageVo.getRecords().forEach(item -> {
            if (BaseConstant.QuartzType.QUARTZ_SIMPLE_SCHEDULE.equals(item.getScheduleType())) {
                item.setScheduleType(stringStringMap.get(item.getScheduleType()));
            }
        });
        return pageVo;
    }

    @Override
    public void startJob(Long id) {
        boolean started = sysScheduleJobMapper.isStarted(id);

        if (started) {
            throw new GlobalException("该任务已启动");
        }

        SysScheduleJob sysScheduleJob = sysScheduleJobMapper.selectById(id);

        configJob(sysScheduleJob);

        sysScheduleJobMapper.startJob(id);
    }

    @Override
    public void stopJob(Long id) {
        boolean started = sysScheduleJobMapper.isStarted(id);

        if (!started) {
            throw new GlobalException("该任务未启动");
        }

        try {
            SysScheduleJob sysScheduleJob = sysScheduleJobMapper.selectById(id);
            JobKey jobKey = new JobKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException("任务停止失败");
        }

        sysScheduleJobMapper.stopJob(id);
    }

    @Override
    public boolean existJobType(String jobType, String jobGroup) {
        LambdaQueryWrapper<SysScheduleJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysScheduleJob::getJobType, jobType);
        wrapper.eq(SysScheduleJob::getJobGroup, jobGroup);
        return sysScheduleJobMapper.exists(wrapper);
    }

    @Override
    public GetJobVo getJob(Long id) {
        return sysScheduleJobMapper.getJob(id);
    }

    @Override
    public void editJob(EditJobRequest request) {
        SysScheduleJob sysScheduleJob = new SysScheduleJob();
        BeanUtils.copyProperties(request, sysScheduleJob);
        sysScheduleJobMapper.updateById(sysScheduleJob);

        if (BaseConstant.Status.STATUS_ENABLE.equals(sysScheduleJob.getStatus())) {
            configJob(sysScheduleJob);
        }
    }

    @Override
    public void deleteJob(Long id) {
        SysScheduleJob sysScheduleJob = sysScheduleJobMapper.selectById(id);

        if (BaseConstant.Status.STATUS_ENABLE.equals(sysScheduleJob.getStatus())) {
            try {
                JobKey jobKey = new JobKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                throw new GlobalException("任务停止失败");
            }
        }

        sysScheduleJobMapper.deleteById(id);
    }

    /**
     * 配置job
     * @param sysScheduleJob
     */
    private void configJob(SysScheduleJob sysScheduleJob) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(sysScheduleJob.getJobClass());

            Date startTime = Optional.ofNullable(sysScheduleJob.getStartTime()).map(item -> Date.from(item.atZone(ZoneId.systemDefault()).toInstant())).orElse(new Date());
            Date endTime = Optional.ofNullable(sysScheduleJob.getEndTime()).map(item -> Date.from(item.atZone(ZoneId.systemDefault()).toInstant())).orElse(null);

            JobKey jobKey = JobKey.jobKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());

            JobDetail jobDetail = JobBuilder
                    .newJob(jobClass)
                    .withIdentity(jobKey)
                    .storeDurably()
                    .build();

            // 导入job所需参数
            JobDataMap jobDataMap = jobDetail.getJobDataMap();

            if (StringUtils.isNotBlank(sysScheduleJob.getJobParam())) {
                Map<String, Object> paramMap = JSON.parseObject(sysScheduleJob.getJobParam());
                paramMap.forEach((k, v) -> {
                    if (v != null && !(v instanceof Serializable)) {
                        throw new GlobalException("任务参数必须可序列化");
                    }
                    jobDataMap.put(k, v);
                });
            }

            Trigger trigger;
            if (BaseConstant.QuartzType.QUARTZ_SIMPLE_SCHEDULE.equals(sysScheduleJob.getScheduleType())) {

                // 根据不同的间隔类型选择不同的定时逻辑
                trigger = getTrigger(startTime, endTime, sysScheduleJob, triggerKey);

            } else {
                // 使用Cron触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(sysScheduleJob.getCron()))
                        .startAt(startTime)
                        .endAt(endTime)
                        .build();
                sysScheduleJob.setCron(sysScheduleJob.getCron());
            }

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException("定时任务启动失败");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(e.getMessage(), e);
        }
    }

    public Trigger getTrigger(Date startTime, Date endTime, SysScheduleJob sysScheduleJob, TriggerKey triggerKey) {
        Trigger trigger;
        switch (QuartzIntervalEnum.map.get(sysScheduleJob.getIntervalType())) {
            case MILLISECONDS -> trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInMilliseconds(sysScheduleJob.getJobInterval())
                            .repeatForever())
                    .startAt(startTime)
                    .endAt(endTime)
                    .build();
            case SECONDS -> trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInSeconds(sysScheduleJob.getJobInterval())
                            .repeatForever())
                    .startAt(startTime)
                    .endAt(endTime)
                    .build();
            case MINUTES -> trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInMinutes(sysScheduleJob.getJobInterval())
                            .repeatForever())
                    .startAt(startTime)
                    .endAt(endTime)
                    .build();
            case HOURS -> trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInHours(sysScheduleJob.getJobInterval())
                            .repeatForever())
                    .startAt(startTime)
                    .endAt(endTime)
                    .build();
            default -> throw new GlobalException("未知的间隔类型");
        }
        return trigger;
    }
}




