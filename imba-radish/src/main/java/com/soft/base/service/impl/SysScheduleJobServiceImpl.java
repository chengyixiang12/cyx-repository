package com.soft.base.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.enums.JobEnum;
import com.soft.base.enums.QuartzIntervalEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysScheduleJobMapper;
import com.soft.base.model.request.CreateJobRequest;
import com.soft.base.model.request.GetQuartzTasksRequest;
import com.soft.base.model.vo.GetQuartzTasksVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysScheduleJobService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    public SysScheduleJobServiceImpl(SysScheduleJobMapper sysScheduleJobMapper,
                                     Scheduler scheduler) {
        this.sysScheduleJobMapper = sysScheduleJobMapper;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createJob(CreateJobRequest request) {
        Date startTime = new Date();
        Date endTime = null;
        try {
            if (StringUtils.isBlank(request.getJobGroup())) {
                request.setJobGroup(BaseConstant.QUARTZ_DEFAULT_GROUP);
            }
            if (request.getStartTime() != null) {
                startTime = Date.from(request.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
            }
            if (request.getEndTime() != null) {
                endTime = Date.from(request.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
            }

            SysScheduleJob sysScheduleJob = new SysScheduleJob();

            sysScheduleJob.setJobType(request.getJobType());
            sysScheduleJob.setJobGroup(request.getJobGroup());
            sysScheduleJob.setJobName(request.getJobName());
            sysScheduleJob.setJobParam(request.getJobParam());
            sysScheduleJob.setStatus(BaseConstant.QuartzStatus.QUARTZ_START);
            sysScheduleJob.setScheduleType(request.getScheduleType());

            LambdaQueryWrapper<SysScheduleJob> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysScheduleJob::getJobGroup, request.getJobGroup());
            wrapper.eq(SysScheduleJob::getJobName, request.getJobName());
            if (sysScheduleJobMapper.exists(wrapper)) {
                throw new GlobalException("该任务已存在");
            }

            JobKey jobKey = JobKey.jobKey(request.getJobName(), request.getJobGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(request.getJobName(), request.getJobGroup());

            JobDetail jobDetail = JobBuilder
                    .newJob(JobEnum.getJobClass(request.getJobType()))
                    .withIdentity(jobKey)
                    .storeDurably()
                    .build();

            // 导入job所需参数
            JobDataMap jobDataMap = jobDetail.getJobDataMap();

            if (StringUtils.isNotBlank(request.getJobParam())) {
                Map<String, Object> paramMap = JSON.parseObject(request.getJobParam());
                paramMap.forEach((k, v) -> {
                    if (v != null && !(v instanceof Serializable)) {
                        throw new GlobalException("任务参数必须可序列化");
                    }
                    jobDataMap.put(k, v);
                });
            }

            Trigger trigger;
            if (BaseConstant.QuartzType.QUARTZ_SIMPLE_SCHEDULE.equals(request.getScheduleType())) {

                // 根据不同的间隔类型选择不同的定时逻辑
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

                sysScheduleJob.setJobInterval(request.getJobInterval());
                sysScheduleJob.setIntervalType(request.getIntervalType());
                sysScheduleJob.setStartTime(request.getStartTime());
                sysScheduleJob.setEndTime(request.getEndTime());

            } else {
                // 使用Cron触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(request.getCron()))
                        .startAt(startTime)
                        .endAt(endTime)
                        .build();
                sysScheduleJob.setCron(request.getCron());
            }

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();

            sysScheduleJobMapper.insert(sysScheduleJob);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException("定时任务创建失败");
        }
    }

    @Override
    public PageVo<GetQuartzTasksVo> getQuartzTasks(GetQuartzTasksRequest request) {
        Page<GetQuartzTasksVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysScheduleJobMapper.getQuartzTasks(page, request);
        PageVo<GetQuartzTasksVo> pageVo = new PageVo<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public void startJob(Long id) {
        boolean started = sysScheduleJobMapper.isStarted(id);

        if (started) {
            throw new GlobalException("该任务已启动");
        }

        SysScheduleJob sysScheduleJob = sysScheduleJobMapper.selectById(id);

        try {
            Date startTime = Optional.ofNullable(sysScheduleJob.getStartTime()).map(item -> Date.from(item.atZone(ZoneId.systemDefault()).toInstant())).orElse(new Date());
            Date endTime = Optional.ofNullable(sysScheduleJob.getEndTime()).map(item -> Date.from(item.atZone(ZoneId.systemDefault()).toInstant())).orElse(null);

            JobKey jobKey = JobKey.jobKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());

            JobDetail jobDetail = JobBuilder
                    .newJob(JobEnum.getJobClass(sysScheduleJob.getJobType()))
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
        }

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
}




