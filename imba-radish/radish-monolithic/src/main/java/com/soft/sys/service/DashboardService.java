package com.soft.sys.service;

import com.soft.sys.model.vo.DashboardStatsVO;
import com.soft.sys.model.vo.RecentLoginVO;

import java.util.List;

/**
 * @author cyx
 * @description 首页数据服务
 * @date 2026-11-05
 */
public interface DashboardService {

    /**
     * 首页统计数据：用户/文件/定时任务总数 + 当前 QPS
     */
    DashboardStatsVO getStats();

    /**
     * 当前用户最近 5 次登录记录（来自登录日志）
     */
    List<RecentLoginVO> getRecentLogins();
}
