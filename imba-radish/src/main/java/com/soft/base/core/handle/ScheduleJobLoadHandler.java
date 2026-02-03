package com.soft.base.core.handle;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.enums.QuartzIntervalEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.service.SysScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: 服务启动时，加载数据库中存储的调度任务
 * @DateTime: 2025/7/14 13:41
 **/

@Component
@Slf4j
public class ScheduleJobLoadHandler implements CommandLineRunner {

    private final SysScheduleJobService sysScheduleJobService;

    private final Scheduler scheduler;

    @Autowired
    public ScheduleJobLoadHandler(SysScheduleJobService sysScheduleJobService,
                                  Scheduler scheduler) {
        this.sysScheduleJobService = sysScheduleJobService;
        this.scheduler = scheduler;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("加载定时任务");
        Date startTime = new Date();
        Date endTime = null;
        LambdaQueryWrapper<SysScheduleJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysScheduleJob::getStatus, BaseConstant.Status.STATUS_ENABLE);
        List<SysScheduleJob> sysScheduleJobs = sysScheduleJobService.list(wrapper);

        if (CollectionUtil.isEmpty(sysScheduleJobs)) {
            return;
        }

        for (SysScheduleJob sysScheduleJob : sysScheduleJobs) {

            @SuppressWarnings("unchecked")
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(sysScheduleJob.getJobClass());

            if (sysScheduleJob.getStartTime() != null) {
                startTime = Date.from(sysScheduleJob.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
            }
            if (sysScheduleJob.getEndTime() != null) {
                endTime = Date.from(sysScheduleJob.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
            }
            JobKey jobKey = JobKey.jobKey(sysScheduleJob.getJobType(), sysScheduleJob.getJobGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(sysScheduleJob.getJobType(), sysScheduleJob.getJobGroup());

            JobDetail jobDetail = JobBuilder
                    .newJob(jobClass)
                    .withIdentity(jobKey)
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
            }

            scheduler.scheduleJob(jobDetail, trigger);
            log.debug("{}-{}定时任务加载", sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
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
