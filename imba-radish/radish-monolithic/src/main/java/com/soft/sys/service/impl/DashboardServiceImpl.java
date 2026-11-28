package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft.common.enums.LogTypeEnum;
import com.soft.sys.entity.SysDialogue;
import com.soft.sys.entity.SysLog;
import com.soft.sys.mapper.SysDialogueMapper;
import com.soft.sys.mapper.SysLogMapper;
import com.soft.sys.model.vo.RecentDialogueVO;
import com.soft.sys.model.vo.RecentLoginVO;
import com.soft.sys.service.DashboardService;
import com.soft.sys.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cyx
 * @description 首页数据服务实现（所有查询都带当前登录人条件）
 * @date 2026-11-05
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SysLogMapper sysLogMapper;
    private final SysDialogueMapper sysDialogueMapper;
    private final SecurityUtil securityUtil;

    @Override
    public List<RecentLoginVO> getRecentLogins() {
        Long userId = securityUtil.getUserInfo().getId();
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .eq(SysLog::getCreateBy, userId)
                .eq(SysLog::getType, LogTypeEnum.LOGIN.getCode())
                .orderByDesc(SysLog::getCreateTime)
                .last("LIMIT 5");
        return sysLogMapper.selectList(wrapper).stream().map(log -> {
            RecentLoginVO item = new RecentLoginVO();
            item.setLoginTime(log.getCreateTime());
            item.setIpAddress(log.getIpAddress());
            item.setOsBrowserInfo(log.getOsBrowserInfo());
            return item;
        }).toList();
    }

    @Override
    public List<RecentDialogueVO> getRecentDialogues() {
        // 首页是静态路由，所有登录用户都能访问，这里必须强制按本人过滤，
        // 不能像「历史对话」列表那样对管理员放开全量数据。
        Long userId = securityUtil.getUserInfo().getId();
        LambdaQueryWrapper<SysDialogue> wrapper = new LambdaQueryWrapper<SysDialogue>()
                .eq(SysDialogue::getCreateBy, userId)
                .orderByDesc(SysDialogue::getCreateTime)
                .last("LIMIT 5");
        return sysDialogueMapper.selectList(wrapper).stream().map(dialogue -> {
            RecentDialogueVO item = new RecentDialogueVO();
            item.setId(dialogue.getId());
            item.setTitle(dialogue.getTitle());
            item.setCreateTime(dialogue.getCreateTime());
            return item;
        }).toList();
    }
}
