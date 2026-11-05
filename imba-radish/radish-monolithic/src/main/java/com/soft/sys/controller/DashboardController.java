package com.soft.sys.controller;

import com.soft.common.resultapi.R;
import com.soft.sys.model.vo.DashboardStatsVO;
import com.soft.sys.model.vo.RecentLoginVO;
import com.soft.sys.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cyx
 * @description 首页数据
 * @date 2026-11-05
 */
@RestController
@RequestMapping(value = "/dashboard")
@Slf4j
@Validated
@Tag(name = "首页")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/stats")
    @Operation(summary = "首页统计数据：用户/文件/定时任务总数 + 当前QPS")
    public R<DashboardStatsVO> stats() {
        return R.ok(dashboardService.getStats());
    }

    @GetMapping(value = "/recentLogins")
    @Operation(summary = "当前用户最近5次登录记录")
    public R<List<RecentLoginVO>> recentLogins() {
        return R.ok(dashboardService.getRecentLogins());
    }
}
