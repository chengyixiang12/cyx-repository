package com.soft.sys.controller;

import com.soft.common.resultapi.R;
import com.soft.sys.model.vo.ComponentOverviewVO;
import com.soft.sys.model.vo.MetricOverviewVO;
import com.soft.sys.model.vo.TrendSeriesVO;
import com.soft.sys.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cyx
 * @description 系统监控
 * @date 2026-11-05
 */
@RestController
@RequestMapping(value = "/actuator")
@Slf4j
@Validated
@Tag(name = "监控")
@RequiredArgsConstructor
public class SysActuatorController {

    private final MonitorService monitorService;

    @GetMapping(value = "/overview")
    @Operation(summary = "实时总览（读内存快照，不查库）")
    public R<MetricOverviewVO> overview() {
        return R.ok(monitorService.getOverview());
    }

    @GetMapping(value = "/trend")
    @Operation(summary = "指标趋势，按时间范围自动选择原始或聚合数据")
    public R<List<TrendSeriesVO>> trend(
            @Parameter(description = "时间范围：15m/1h/6h/1d/7d/30d", in = ParameterIn.QUERY)
            @RequestParam(value = "range", required = false, defaultValue = "15m") String range,
            @Parameter(description = "指标标识，逗号分隔", in = ParameterIn.QUERY)
            @RequestParam(value = "metrics", required = false) String metrics) {
        return R.ok(monitorService.getTrend(range, metrics));
    }

    @GetMapping(value = "/components")
    @Operation(summary = "组件当前状态与最近的状态变更")
    public R<ComponentOverviewVO> components() {
        return R.ok(monitorService.getComponents());
    }
}
