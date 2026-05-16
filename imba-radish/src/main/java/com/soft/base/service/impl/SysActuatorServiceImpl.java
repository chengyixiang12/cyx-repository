package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysActuator;
import com.soft.base.mapper.SysActuatorMapper;
import com.soft.base.model.request.ListActuatorPageRequest;
import com.soft.base.model.vo.GetLatestActuatorMetricVO;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.model.vo.ListUsageTrendVO;
import com.soft.base.model.vo.PageVO;
import com.soft.base.service.SysActuatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 程益祥
 * @description 针对表【sys_actuator(系统监控表)】的数据库操作Service实现
 * @createDate 2026-04-30 23:04:53
 */
@Service
@RequiredArgsConstructor
public class SysActuatorServiceImpl extends ServiceImpl<SysActuatorMapper, SysActuator>
        implements SysActuatorService {

    private final SysActuatorMapper sysActuatorMapper;

    @Override
    public List<ListActuatorVO> listActuator(LocalDateTime startTime, LocalDateTime endTime) {
        return sysActuatorMapper.listActuator(startTime, endTime);
    }

    @Override
    public PageVO<ListActuatorVO> listActuatorPage(ListActuatorPageRequest request) {
        IPage<ListActuatorVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        sysActuatorMapper.listActuatorPage(page, request);
        PageVO<ListActuatorVO> pageVO = new PageVO<>();
        pageVO.setRecords(page.getRecords());
        pageVO.setTotal(page.getTotal());
        return pageVO;
    }

    @Override
    public GetLatestActuatorMetricVO getLatestActuatorMetric() {
        return sysActuatorMapper.getLatestActuatorMetric();
    }

    @Override
    public List<ListUsageTrendVO> listCpuTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return sysActuatorMapper.listCpuTrend(startTime, endTime);
    }

    @Override
    public List<ListUsageTrendVO> listMemoryTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return sysActuatorMapper.listMemoryTrend(startTime, endTime);
    }
}




