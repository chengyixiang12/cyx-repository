package com.soft.base.quartz.listener;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2025/11/11
 * @description
 */
@Component
public class MdcJobListener implements JobListener {
    @Override
    public String getName() {
        return "mdcJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        // 在Job执行前设置上下文
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        MDC.put("jobName", jobDetail.getKey().getName());
        MDC.put("jobGroup", jobDetail.getKey().getGroup());
        MDC.put("fireTime", String.valueOf(jobExecutionContext.getFireTime().getTime()));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        // Job执行完成后清理MDC
        MDC.clear();
    }
}
