package com.soft.base.quartz;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.enums.JobEnum;
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
public class ScheduleJobLoader implements CommandLineRunner {

    private final SysScheduleJobService sysScheduleJobService;

    private final Scheduler scheduler;

    @Autowired
    public ScheduleJobLoader(SysScheduleJobService sysScheduleJobService,
                             Scheduler scheduler) {
        this.sysScheduleJobService = sysScheduleJobService;
        this.scheduler = scheduler;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("开始加载定时任务");
        Date startTime = new Date();
        Date endTime = null;
        List<SysScheduleJob> sysScheduleJobs = sysScheduleJobService.list();
        if (CollectionUtil.isNotEmpty(sysScheduleJobs)) {
            for (SysScheduleJob sysScheduleJob : sysScheduleJobs) {
                if (sysScheduleJob.getStartTime() != null) {
                    startTime = Date.from(sysScheduleJob.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
                }
                if (sysScheduleJob.getEndTime() != null) {
                    endTime = Date.from(sysScheduleJob.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
                }
                JobKey jobKey = JobKey.jobKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
                TriggerKey triggerKey = TriggerKey.triggerKey(sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());

                JobDetail jobDetail = JobBuilder
                        .newJob(JobEnum.getJobClass(sysScheduleJob.getJobType()))
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
                if (BaseConstant.QUARTZ_SIMPLE_SCHEDULE.equals(sysScheduleJob.getScheduleType())) {

                    // 根据不同的间隔类型选择不同的定时逻辑
                    switch (sysScheduleJob.getIntervalType()) {
                        case BaseConstant.QUARTZ_SIMPLE_INTERVAL_TYPE_MILLISECONDS -> trigger = TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .withSchedule(SimpleScheduleBuilder
                                        .simpleSchedule()
                                        .withIntervalInMilliseconds(sysScheduleJob.getJobInterval())
                                        .repeatForever())
                                .startAt(startTime)
                                .endAt(endTime)
                                .build();
                        case BaseConstant.QUARTZ_SIMPLE_INTERVAL_TYPE_SECONDS -> trigger = TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .withSchedule(SimpleScheduleBuilder
                                        .simpleSchedule()
                                        .withIntervalInSeconds(sysScheduleJob.getJobInterval())
                                        .repeatForever())
                                .startAt(startTime)
                                .endAt(endTime)
                                .build();
                        case BaseConstant.QUARTZ_SIMPLE_INTERVAL_TYPE_MINUTES -> trigger = TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .withSchedule(SimpleScheduleBuilder
                                        .simpleSchedule()
                                        .withIntervalInMinutes(sysScheduleJob.getJobInterval())
                                        .repeatForever())
                                .startAt(startTime)
                                .endAt(endTime)
                                .build();
                        case BaseConstant.QUARTZ_SIMPLE_INTERVAL_TYPE_HOURS -> trigger = TriggerBuilder.newTrigger()
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
                }

                scheduler.scheduleJob(jobDetail, trigger);
                log.info("{}-{}定时任务加载成功", sysScheduleJob.getJobName(), sysScheduleJob.getJobGroup());
            }
        }
    }
}
