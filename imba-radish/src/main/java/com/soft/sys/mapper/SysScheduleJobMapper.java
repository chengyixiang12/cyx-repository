package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.SysScheduleJob;
import com.soft.sys.model.request.GetQuartzTasksDTO;
import com.soft.sys.model.vo.GetJobVO;
import com.soft.sys.model.vo.GetQuartzTasksVO;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Mapper
* @createDate 2025-07-11 19:56:32
* @Entity com.soft.base.entity.SysScheduleJob
*/
public interface SysScheduleJobMapper extends BaseMapper<SysScheduleJob> {

    Page<GetQuartzTasksVO> getQuartzTasks(@Param("page") Page<GetQuartzTasksVO> page,
                                          @Param("request") GetQuartzTasksDTO request);

    boolean isStarted(@Param("id") Long id);

    void startJob(@Param("id") Long id);

    void stopJob(@Param("id") Long id);

    GetJobVO getJob(@Param("id") Long id);
}




