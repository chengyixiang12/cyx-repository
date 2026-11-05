package com.soft.sys.monitor;

import com.soft.sys.mapper.SysMetricRollupMapper;
import com.soft.sys.mapper.SysMetricSampleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 指标聚合与清理
 * <p>
 * 聚合采用“先删桶、再插入”的方式保证幂等：同一个桶重跑只会覆盖，不会产生重复行，
 * 也不依赖 INSERT ... ON DUPLICATE KEY UPDATE 的语法差异。
 * @date 2026-11-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorAggregateService {

    private static final String GRANULARITY_5M = "5m";
    private static final String GRANULARITY_1H = "1h";

    private final SysMetricSampleMapper sampleMapper;
    private final SysMetricRollupMapper rollupMapper;

    /**
     * 5 分钟聚合：从原始样本聚合
     */
    @Transactional(rollbackFor = Exception.class)
    public void rebuild5m(LocalDateTime start, LocalDateTime end) {
        rollupMapper.deleteRange(GRANULARITY_5M, start, end);
        int rows = sampleMapper.insert5mRollup(start, end);
        log.debug("5 分钟聚合完成：{} 行", rows);
    }

    /**
     * 1 小时聚合：从 5 分钟聚合数据汇总（原始样本只留 3 天，撑不住一年）
     */
    @Transactional(rollbackFor = Exception.class)
    public void rebuild1h(LocalDateTime start, LocalDateTime end) {
        rollupMapper.deleteRange(GRANULARITY_1H, start, end);
        int rows = rollupMapper.insert1hRollup(start, end);
        log.debug("1 小时聚合完成：{} 行", rows);
    }

    /**
     * 分批清理过期数据，避免一条 DELETE 锁住大段区间
     */
    @Transactional(rollbackFor = Exception.class)
    public int purgeSample(LocalDateTime before, int batch) {
        int total = 0;
        while (true) {
            int deleted = sampleMapper.deleteBefore(before, batch);
            total += deleted;
            if (deleted < batch) {
                return total;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int purgeRollup(String granularity, LocalDateTime before, int batch) {
        int total = 0;
        while (true) {
            int deleted = rollupMapper.deleteBefore(granularity, before, batch);
            total += deleted;
            if (deleted < batch) {
                return total;
            }
        }
    }
}
