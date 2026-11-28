package com.soft.sys.service;

import com.soft.sys.model.vo.RecentDialogueVO;
import com.soft.sys.model.vo.RecentLoginVO;

import java.util.List;

/**
 * @author cyx
 * @description 首页数据（个人维度）
 * @date 2026-11-05
 */
public interface DashboardService {

    /**
     * 当前用户最近 5 次登录记录（来自登录日志）
     */
    List<RecentLoginVO> getRecentLogins();

    /**
     * 当前用户最近 5 条对话（强制按登录人过滤，管理员也不跨用户）
     */
    List<RecentDialogueVO> getRecentDialogues();
}
