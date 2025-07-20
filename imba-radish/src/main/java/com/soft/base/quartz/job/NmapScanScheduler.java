package com.soft.base.quartz.job;

import com.soft.base.model.dto.FileDetailDto;
import com.soft.base.service.SysFileService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/7/14 14:22
 **/
@Component
@Slf4j
public class NmapScanScheduler implements Job {

    private SysFileService sysFileService;

    @Autowired
    public void setSysFileService(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        FileDetailDto fileDetailById = sysFileService.getFileDetailById(1L);
        log.info("第一个文件的详情：{}", fileDetailById.toString());
    }
}
