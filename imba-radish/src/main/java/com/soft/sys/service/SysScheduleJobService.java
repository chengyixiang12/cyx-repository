package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysScheduleJob;
import com.soft.sys.model.request.CreateJobDTO;
import com.soft.sys.model.request.EditJobDTO;
import com.soft.sys.model.request.GetQuartzTasksDTO;
import com.soft.sys.model.vo.GetJobVO;
import com.soft.sys.model.vo.GetQuartzTasksVO;
import com.soft.sys.model.vo.PageVO;

/**
* @author cyq
* @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Service
* @createDate 2025-07-11 19:56:32
*/
public interface SysScheduleJobService extends IService<SysScheduleJob> {

    void createJob(CreateJobDTO request);

    PageVO<GetQuartzTasksVO> getQuartzTasks(GetQuartzTasksDTO request);

    void startJob(Long id);

    void stopJob(Long id);

    boolean existJobType(String jobType, String jobGroup);

    GetJobVO getJob(Long id);

    void editJob(EditJobDTO request);

    void deleteJob(Long id);
}
