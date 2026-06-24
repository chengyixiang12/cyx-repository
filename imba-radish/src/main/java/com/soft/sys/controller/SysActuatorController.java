package com.soft.sys.controller;

import com.soft.sys.model.request.ListActuatorPageRequest;
import com.soft.sys.model.vo.GetLatestActuatorMetricVO;
import com.soft.sys.model.vo.ListActuatorVO;
import com.soft.sys.model.vo.ListUsageTrendVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.resultapi.R;
import com.soft.sys.service.SysActuatorService;
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
        return R.ok(sysActuatorService.listCpuTrend(startTime, endTime));
    }

    @GetMapping(value = "/listMemoryTrend")
    @Operation(summary = "获取jvm内存趋势")
    @Parameters(value = {
            @Parameter(name = "startTime", description = "开始日期", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "endTime", description = "结束日期", required = true, in = ParameterIn.QUERY)
    })
    public R<List<ListUsageTrendVO>> listMemoryTrend(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                      @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(sysActuatorService.listMemoryTrend(startTime, endTime));
    }

    @GetMapping(value = "/listHeapMemoryTrend")
    @Operation(summary = "获取堆内存趋势")
    @Parameters(value = {
            @Parameter(name = "startTime", description = "开始日期", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "endTime", description = "结束日期", required = true, in = ParameterIn.QUERY)
    })
    public R<List<ListUsageTrendVO>> listHeapMemoryTrend(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                         @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(sysActuatorService.listHeapMemoryTrend(startTime, endTime));
    }

    @GetMapping(value = "/listMetaspaceMemoryTrend")
    @Operation(summary = "获取元空间内存趋势")
    @Parameters(value = {
            @Parameter(name = "startTime", description = "开始日期", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "endTime", description = "结束日期", required = true, in = ParameterIn.QUERY)
    })
    public R<List<ListUsageTrendVO>> listMetaspaceMemoryTrend(@RequestParam(value = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                         @RequestParam(value = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return R.ok(sysActuatorService.listMetaspaceMemoryTrend(startTime, endTime));
    }
}
