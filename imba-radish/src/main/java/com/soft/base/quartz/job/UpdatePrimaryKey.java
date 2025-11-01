package com.soft.base.quartz.job;


import com.soft.base.entity.*;
import com.soft.base.service.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
