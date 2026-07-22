package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysActuator;
import com.soft.sys.model.request.ListActuatorPageDTO;
import com.soft.sys.model.vo.GetLatestActuatorMetricVO;
import com.soft.sys.model.vo.ListActuatorVO;
import com.soft.sys.model.vo.ListUsageTrendVO;
import com.soft.sys.model.vo.PageVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @author 程益祥
* @description 针对表【sys_actuator(系统监控表)】的数据库操作Service
* @createDate 2026-04-30 23:04:53
*/
public interface SysActuatorService extends IService<SysActuator> {

    List<ListActuatorVO> listActuator(LocalDateTime startTime, LocalDateTime endTime);

    PageVO<ListActuatorVO> listActuatorPage(ListActuatorPageDTO request);

    GetLatestActuatorMetricVO getLatestActuatorMetric();

    List<ListUsageTrendVO> listCpuTrend(LocalDateTime startTime, LocalDateTime endTime);

    List<ListUsageTrendVO> listMemoryTrend(LocalDateTime startTime, LocalDateTime endTime);

    List<ListUsageTrendVO> listHeapMemoryTrend(LocalDateTime startTime, LocalDateTime endTime);

    List<ListUsageTrendVO> listMetaspaceMemoryTrend(LocalDateTime startTime, LocalDateTime endTime);

    void deleteOneMonthAgo(Date date);
}
