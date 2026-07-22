package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.SysActuator;
import com.soft.sys.model.request.ListActuatorPageDTO;
import com.soft.sys.model.vo.GetLatestActuatorMetricVO;
import com.soft.sys.model.vo.ListActuatorVO;
import com.soft.sys.model.vo.ListUsageTrendVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
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

    Page<ListActuatorVO> listActuatorPage(IPage<ListActuatorVO> page, @Param("request") ListActuatorPageDTO request);

    GetLatestActuatorMetricVO getLatestActuatorMetric();

    List<ListUsageTrendVO> listCpuTrend(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    List<ListUsageTrendVO> listMemoryTrend(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    List<ListUsageTrendVO> listHeapMemoryTrend(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    List<ListUsageTrendVO> listMetaspaceMemoryTrend(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    void deleteOneMonthAgo(@Param("date") Date date);
}




