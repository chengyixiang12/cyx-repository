package com.soft.base.service;

import com.soft.base.entity.SysScheduleJob;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.request.CreateJobRequest;
import com.soft.base.model.request.GetQuartzTasksRequest;
import com.soft.base.model.vo.GetQuartzTasksVo;
import com.soft.base.model.vo.PageVo;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Service
* @createDate 2025-07-11 19:56:32
*/
public interface SysScheduleJobService extends IService<SysScheduleJob> {

    void createJob(CreateJobRequest request);

    PageVo<GetQuartzTasksVo> getQuartzTasks(GetQuartzTasksRequest request);
}
