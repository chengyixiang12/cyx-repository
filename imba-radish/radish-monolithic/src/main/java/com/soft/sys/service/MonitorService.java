package com.soft.sys.service;

import com.soft.sys.model.vo.ComponentOverviewVO;
import com.soft.sys.model.vo.MetricOverviewVO;
import com.soft.sys.model.vo.TrendSeriesVO;

import java.util.List;

/**
 * @author cyx
 * @description 系统监控
 * @date 2026-11-05
 */
public interface MonitorService {

    /**
     * 实时总览，读内存快照不查库
     */
    MetricOverviewVO getOverview();

    /**
     * 趋势数据，按时间范围自动选择原始样本或聚合数据
     *
     * @param range   时间范围；15m / 1h / 6h / 1d / 7d / 30d
     * @param metrics 指标标识，逗号分隔；为空取默认集合
     */
    List<TrendSeriesVO> getTrend(String range, String metrics);

    /**
     * 组件当前状态与最近的状态变更
     */
    ComponentOverviewVO getComponents();
}
