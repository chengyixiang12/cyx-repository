package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft.common.enums.LogTypeEnum;
import com.soft.sys.entity.SysLog;
import com.soft.sys.entity.SysFile;
import com.soft.sys.entity.SysScheduleJob;
import com.soft.sys.entity.SysUser;
import com.soft.sys.mapper.SysFileMapper;
import com.soft.sys.mapper.SysLogMapper;
import com.soft.sys.mapper.SysScheduleJobMapper;
import com.soft.sys.mapper.SysUsersMapper;
import com.soft.sys.model.vo.DashboardStatsVO;
import com.soft.sys.model.vo.MetricOverviewVO;
import com.soft.sys.model.vo.RecentLoginVO;
import com.soft.sys.monitor.MetricSnapshotHolder;
import com.soft.sys.service.DashboardService;
import com.soft.sys.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cyx
 * @description 首页数据服务实现
 * @date 2026-11-05
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SysUsersMapper sysUsersMapper;
    private final SysFileMapper sysFileMapper;
    private final SysScheduleJobMapper sysScheduleJobMapper;
    private final SysLogMapper sysLogMapper;
    private final MetricSnapshotHolder metricSnapshotHolder;
    private final SecurityUtil securityUtil;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setUserCount(sysUsersMapper.selectCount(null));
        vo.setFileCount(sysFileMapper.selectCount(null));
        vo.setJobCount(sysScheduleJobMapper.selectCount(null));
        MetricOverviewVO overview = metricSnapshotHolder.current();
        vo.setQps(overview.getQps() == null ? 0D : overview.getQps());
        return vo;
    }

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
}
