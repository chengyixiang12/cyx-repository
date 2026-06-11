package com.soft.sys.quartz.job;


import com.soft.sys.service.SysScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2025/11/1
 * @description
 */
@Component
@Slf4j
public class UpdatePrimaryKey implements Job {

    private SysScheduleJobService service;

    @Autowired
    public void setService(SysScheduleJobService service) {
        this.service = service;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("修改主键");
    }
}
