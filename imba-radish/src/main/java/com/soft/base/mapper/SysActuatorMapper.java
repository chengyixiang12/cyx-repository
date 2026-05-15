package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.entity.SysActuator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.model.request.ListActuatorPageRequest;
import com.soft.base.model.vo.GetLatestActuatorMetricVO;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.model.vo.ListUsageTrendVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 程益祥
* @description 针对表【sys_actuator(系统监控表)】的数据库操作Mapper
* @createDate 2026-04-30 23:04:53
* @Entity com.soft.base.entity.SysActuator
*/
public interface SysActuatorMapper extends BaseMapper<SysActuator> {

    List<ListActuatorVO> listActuator(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    Page<ListActuatorVO> listActuatorPage(IPage<ListActuatorVO> page, @Param("request") ListActuatorPageRequest request);

    GetLatestActuatorMetricVO getLatestActuatorMetric();

    List<ListUsageTrendVO> listCpuTrend(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    List<ListUsageTrendVO> listMemeryTrend(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
}




