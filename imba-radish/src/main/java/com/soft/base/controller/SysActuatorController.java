package com.soft.base.controller;

import com.soft.base.constants.BaseConstant;
import com.soft.base.model.request.ListActuatorPageRequest;
import com.soft.base.model.vo.GetLatestActuatorMetricVO;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.model.vo.ListUsageTrendVO;
import com.soft.base.model.vo.PageVO;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysActuatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cyx
 * @description:
 * @date 2026-05-01
 */
@RestController
@RequestMapping(value = "/actuator")
@Slf4j
@Validated
@Tag(name = "监控")
@RequiredArgsConstructor
public class SysActuatorController {

    private final SysActuatorService sysActuatorService;

    @GetMapping(value = "/listActuators")
    @Operation(summary = "获取监控记录")
    public R<List<ListActuatorVO>> listActuator(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(sysActuatorService.listActuator(startTime, endTime));
    }

    @PostMapping(value = "/listActuatorPage")
    @Operation(summary = "获取监控记录")
    public R<PageVO<ListActuatorVO>> listActuatorPage(@RequestBody ListActuatorPageRequest request) {
        return R.ok(sysActuatorService.listActuatorPage(request));
    }

    @GetMapping(value = "/getLatestActuatorMetric")
    @Operation(summary = "获取最新的监控指标")
    public R<GetLatestActuatorMetricVO> getLatestActuatorMetric() {
        return R.ok(sysActuatorService.getLatestActuatorMetric());
    }

    @GetMapping(value = "/listCpuTrend")
    @Operation(summary = "获取cpu趋势")
    @Parameters(value = {
            @Parameter(name = "startTime", description = "开始日期", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "endTime", description = "结束日期", required = true, in = ParameterIn.QUERY)
    })
    public R<List<ListUsageTrendVO>> listCpuTrend(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                  @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(downsample(sysActuatorService.listCpuTrend(startTime, endTime), BaseConstant.Actuator.MAX_POINTS));
    }

    @GetMapping(value = "/listMemeryTrend")
    @Operation(summary = "获取jvm内存趋势")
    @Parameters(value = {
            @Parameter(name = "startTime", description = "开始日期", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "endTime", description = "结束日期", required = true, in = ParameterIn.QUERY)
    })
    public R<List<ListUsageTrendVO>> listMemeryTrend(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                      @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(downsample(sysActuatorService.listMemeryTrend(startTime, endTime), BaseConstant.Actuator.MAX_POINTS));
    }

    /**
     * 对时间序列数据应用 LTTB 降采样
     *
     * @param data      原始数据（按时间升序）
     * @param maxPoints 希望保留的最大点数（包含首尾，至少为 2）
     * @return 降采样后的数据
     */
    private List<ListUsageTrendVO> downsample(List<ListUsageTrendVO> data, int maxPoints) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        // 点数不超过限制或限制小于 2，直接返回
        if (data.size() <= maxPoints || maxPoints < 2) {
            return data;
        }

        int dataSize = data.size();
        List<ListUsageTrendVO> sampled = new ArrayList<>(maxPoints);
        sampled.add(data.get(0));  // 首点必选

        // 将中间数据分入 (maxPoints - 2) 个桶
        double bucketSize = (double) (dataSize - 2) / (maxPoints - 2);
        int a = 0; // 上一个被选中的点的索引

        for (int i = 1; i < maxPoints - 1; i++) {
            // 当前桶在原始数据中的范围 [rangeStart, rangeEnd)
            int rangeStart = (int) Math.floor(i * bucketSize) + 1;
            int rangeEnd   = (int) Math.floor((i + 1) * bucketSize) + 1;
            if (rangeEnd > dataSize - 1) {
                rangeEnd = dataSize - 1;
            }

            // 下一个桶的平均点（用于计算三角形面积）
            int avgRangeStart = rangeEnd;
            int avgRangeEnd   = (int) Math.floor((i + 2) * bucketSize) + 1;
            if (avgRangeEnd > dataSize - 1) {
                avgRangeEnd = dataSize - 1;
            }
            double avgX = 0, avgY = 0;
            int avgCount = avgRangeEnd - avgRangeStart;
            for (int j = avgRangeStart; j < avgRangeEnd; j++) {
                // X 轴直接使用索引（等间隔时间序列）
                avgX += j;
                avgY += data.get(j).getUsageRate();
            }
            avgX /= avgCount;
            avgY /= avgCount;

            // 在桶内选择与三角形面积最大的点
            double maxArea = -1;
            int maxIndex = rangeStart;
            ListUsageTrendVO pointA = data.get(a); // 上一个已选点
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
            a = maxIndex; // 更新已选点
        }

        sampled.add(data.get(dataSize - 1)); // 尾点必选
        return sampled;
    }

    /**
     * 计算三点构成的三角形面积（使用坐标，X 为索引，Y 为使用率）
     */
    private double triangleArea(double x1, double y1, double x2, double y2,
                                       double x3, double y3) {
        return Math.abs((x1 - x3) * (y2 - y1) - (x1 - x2) * (y3 - y1)) * 0.5;
    }
}
