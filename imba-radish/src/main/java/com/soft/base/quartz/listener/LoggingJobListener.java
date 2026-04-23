package com.soft.base.quartz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft.base.entity.SysScheduleJob;
import com.soft.base.entity.SysScheduleRecord;
import com.soft.base.quartz.log.QuartzAppender;
import com.soft.base.service.SysScheduleJobService;
import com.soft.base.service.SysScheduleRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author cyq
 * @date 2025/11/11
 * @description
 */
@Slf4j
@RequiredArgsConstructor
public class LoggingJobListener implements JobListener {

    private final SysScheduleRecordService sysScheduleRecordService;

    private final SysScheduleJobService sysScheduleJobService;

    @Override
    public String getName() {
        return "LoggingJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        // 设置任务相关 MDC 信息
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        String name = jobDetail.getKey().getName();
        String group = jobDetail.getKey().getGroup();
        MDC.put("jobName", name);
        MDC.put("jobGroup", group);
        MDC.put("fireTime", String.valueOf(jobExecutionContext.getFireTime().getTime()));

        // 启动日志收集（会同时设置 MDC 标记和初始化 ThreadLocal）
        QuartzAppender.startCollect();

        // 任务开始的日志也会被收集
        log.info("{}-{}任务开始执行", group, name);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        QuartzAppender.stopAndGet();  // 清理资源
        MDC.clear();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        // 任务结束的日志也会被收集
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        String name = jobDetail.getKey().getName();
        String group = jobDetail.getKey().getGroup();
        log.info("{}-{}任务执行结束", group, name);

        // 停止收集并获取所有日志
        String collectedLogs = QuartzAppender.stopAndGet();
        // 存储到数据库
        saveQuartzLog(jobExecutionContext, collectedLogs, e);

        // 清理 MDC 中的其他信息
        MDC.remove("jobName");
        MDC.remove("jobGroup");
        MDC.remove("fireTime");
    }

    /**
     * 保存定时任务执行记录
     * @param context
     * @param collectedLogs
     * @param e
     */
    private void saveQuartzLog(JobExecutionContext context, String collectedLogs, JobExecutionException e) {
        JobDetail jobDetail = context.getJobDetail();
        String name = jobDetail.getKey().getName();
        String group = jobDetail.getKey().getGroup();
        LocalDateTime startTime = context.getFireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endTime = LocalDateTime.now();
        long executeTime = System.currentTimeMillis() - context.getFireTime().getTime();


        LambdaQueryWrapper<SysScheduleJob> sysScheduleJobWrapper = new LambdaQueryWrapper<>();
        sysScheduleJobWrapper.eq(SysScheduleJob::getJobGroup, group);
        sysScheduleJobWrapper.eq(SysScheduleJob::getJobType, name);
        sysScheduleJobWrapper.select(SysScheduleJob::getId, SysScheduleJob::getJobParam, SysScheduleJob::getJobName);
        SysScheduleJob sysScheduleJob = sysScheduleJobService.getOne(sysScheduleJobWrapper);

        SysScheduleRecord sysScheduleRecord = new SysScheduleRecord();
        sysScheduleRecord.setLogInfo(collectedLogs);
        sysScheduleRecord.setJobId(sysScheduleJob.getId());
        sysScheduleRecord.setJobName(sysScheduleJob.getJobName());
        sysScheduleRecord.setJobGroup(group);
        sysScheduleRecord.setJobType(name);
        sysScheduleRecord.setExecutionTime(executeTime);
        sysScheduleRecord.setStartTime(startTime);
        sysScheduleRecord.setEndTime(endTime);
        sysScheduleRecord.setJobParam(sysScheduleJob.getJobParam());
        sysScheduleRecordService.save(sysScheduleRecord);
    }
}
