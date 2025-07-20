package com.soft.base.service;

import com.soft.base.entity.SysScheduleJob;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.request.CreateJobRequest;
import org.quartz.SchedulerException;

/**
* @author cyq
* @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Service
* @createDate 2025-07-11 19:56:32
*/
public interface SysScheduleJobService extends IService<SysScheduleJob> {

    void createJob(CreateJobRequest request);
}
