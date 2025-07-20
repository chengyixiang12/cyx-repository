package com.soft.base.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/7/11 18:04
 **/
@Slf4j
public class JobHandler implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
