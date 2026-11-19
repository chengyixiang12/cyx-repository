package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.WfInstance;
import com.soft.sys.model.request.GetMyInstancesDTO;
import com.soft.sys.model.vo.WfInstancesVO;
import org.apache.ibatis.annotations.Param;

/**
* @author cyx
* @description 针对表【wf_instance】的数据库操作Mapper
* @createDate 2026-11-19 11:30:00
*/
public interface WfInstanceMapper extends BaseMapper<WfInstance> {

    Page<WfInstancesVO> getMyInstances(@Param("page") IPage<WfInstancesVO> page,
                                       @Param("request") GetMyInstancesDTO request,
                                       @Param("userId") Long userId);
}
