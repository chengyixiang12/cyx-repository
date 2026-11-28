package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.WfTask;
import com.soft.sys.model.request.GetWorkflowTasksDTO;
import com.soft.sys.model.vo.WfTasksVO;
import org.apache.ibatis.annotations.Param;

/**
* @author cyx
* @description 针对表【wf_task】的数据库操作Mapper
* @createDate 2026-11-19 11:30:00
*/
public interface WfTaskMapper extends BaseMapper<WfTask> {

    Page<WfTasksVO> getTodos(@Param("page") IPage<WfTasksVO> page,
                             @Param("request") GetWorkflowTasksDTO request,
                             @Param("userId") Long userId);

    Page<WfTasksVO> getDones(@Param("page") IPage<WfTasksVO> page,
                             @Param("request") GetWorkflowTasksDTO request,
                             @Param("userId") Long userId);
}
