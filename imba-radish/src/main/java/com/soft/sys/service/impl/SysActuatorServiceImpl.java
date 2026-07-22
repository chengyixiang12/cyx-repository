package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.entity.SysActuator;
import com.soft.sys.mapper.SysActuatorMapper;
import com.soft.sys.model.request.ListActuatorPageDTO;
import com.soft.sys.model.vo.GetLatestActuatorMetricVO;
import com.soft.sys.model.vo.ListActuatorVO;
import com.soft.sys.model.vo.ListUsageTrendVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysActuatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    public PageVO<ListActuatorVO> listActuatorPage(ListActuatorPageDTO request) {
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
        return downsample(sysActuatorMapper.listCpuTrend(startTime, endTime),
                calculateMaxPoints(startTime, endTime));
    }

    @Override
    public List<ListUsageTrendVO> listMemoryTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return downsample(sysActuatorMapper.listMemoryTrend(startTime, endTime),
                calculateMaxPoints(startTime, endTime));
    }

    @Override
    public List<ListUsageTrendVO> listHeapMemoryTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return downsample(sysActuatorMapper.listHeapMemoryTrend(startTime, endTime),
                calculateMaxPoints(startTime, endTime));
    }

    @Override
    public List<ListUsageTrendVO> listMetaspaceMemoryTrend(LocalDateTime startTime, LocalDateTime endTime) {
        return downsample(sysActuatorMapper.listMetaspaceMemoryTrend(startTime, endTime),
                calculateMaxPoints(startTime, endTime));
    }

    // ========== 降采样 ==========

    /**
     * 根据时间范围动态计算 LTTB 降采样的目标点数。
     * <p>
     * 时间范围越短点数越少（下限 20），范围越长点数越多（上限 200）。
     * 采用对数函数使增长先快后慢，贴合人眼对不同时间尺度趋势的敏感度。
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 推荐保留的点数
     */
    private int calculateMaxPoints(LocalDateTime startTime, LocalDateTime endTime) {
        long hours = Duration.between(startTime, endTime).toHours();
        if (hours <= 0) hours = 1;
        return (int) Math.clamp(20 + Math.log(hours) * 35, 20, 200);
    }

    /**
     * 对时间序列数据应用 LTTB（Largest-Triangle-Three-Buckets）降采样。
     * 在保留趋势形状的前提下，将数据压缩到 maxPoints 个点。
     *
     * @param data      原始数据（按时间升序）
     * @param maxPoints 希望保留的最大点数（至少为 2）
     * @return 降采样后的数据
     */
    private List<ListUsageTrendVO> downsample(List<ListUsageTrendVO> data, int maxPoints) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        if (data.size() <= maxPoints || maxPoints < 2) {
            return data;
        }

        int dataSize = data.size();
        List<ListUsageTrendVO> sampled = new ArrayList<>(maxPoints);
        sampled.add(data.getFirst());

        double bucketSize = (double) (dataSize - 2) / (maxPoints - 2);
        int a = 0;

        for (int i = 1; i < maxPoints - 1; i++) {
            int rangeStart = (int) Math.floor(i * bucketSize) + 1;
            int rangeEnd = (int) Math.floor((i + 1) * bucketSize) + 1;
            if (rangeEnd > dataSize - 1) {
                rangeEnd = dataSize - 1;
            }

            int avgRangeStart = rangeEnd;
            int avgRangeEnd = (int) Math.floor((i + 2) * bucketSize) + 1;
            if (avgRangeEnd > dataSize - 1) {
                avgRangeEnd = dataSize - 1;
            }
            double avgX = 0, avgY = 0;
            int avgCount = avgRangeEnd - avgRangeStart;
            for (int j = avgRangeStart; j < avgRangeEnd; j++) {
                avgX += j;
                avgY += data.get(j).getUsageRate();
            }
            avgX /= avgCount;
            avgY /= avgCount;

            double maxArea = -1;
            int maxIndex = rangeStart;
            ListUsageTrendVO pointA = data.get(a);
            double ax = a;
            double ay = pointA.getUsageRate();

            for (int j = rangeStart; j < rangeEnd; j++) {
                double area = triangleArea(ax, ay, avgX, avgY, j, data.get(j).getUsageRate());
                if (area > maxArea) {
                    maxArea = area;
                    maxIndex = j;
                }
            }

            sampled.add(data.get(maxIndex));
            a = maxIndex;
        }

        sampled.add(data.get(dataSize - 1));
        return sampled;
    }

    /**
     * 计算三点构成的三角形面积（X 为索引，Y 为使用率）
     */
    private double triangleArea(double x1, double y1, double x2, double y2,
                                       double x3, double y3) {
        return Math.abs((x1 - x3) * (y2 - y1) - (x1 - x2) * (y3 - y1)) * 0.5;
    }

    @Override
    public void deleteOneMonthAgo(Date date) {
        sysActuatorMapper.deleteOneMonthAgo(date);
    }
}




