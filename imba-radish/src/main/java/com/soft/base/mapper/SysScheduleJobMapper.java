package com.soft.base.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.entity.SysScheduleJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.model.request.GetQuartzTasksRequest;
import com.soft.base.model.vo.GetQuartzTasksVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_schedule_job(调度任务表)】的数据库操作Mapper
* @createDate 2025-07-11 19:56:32
* @Entity com.soft.base.entity.SysScheduleJob
*/
public interface SysScheduleJobMapper extends BaseMapper<SysScheduleJob> {

    Page<GetQuartzTasksVo> getQuartzTasks(@Param("page") Page<GetQuartzTasksVo> page,
                                          @Param("request") GetQuartzTasksRequest request);
}




