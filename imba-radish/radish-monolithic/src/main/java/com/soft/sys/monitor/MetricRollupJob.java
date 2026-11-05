package com.soft.sys.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author cyx
 * @description 指标聚合与保留策略
 * <ul>
 *     <li>原始样本 30 秒一条，保留 3 天</li>
 *     <li>5 分钟聚合保留 30 天</li>
 *     <li>1 小时聚合保留 1 年</li>
 * </ul>
 * 每次聚合重算最近若干个桶，这样迟到/补采的样本也能被吸收。
 * @date 2026-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricRollupJob {

    private static final int PURGE_BATCH = 5000;

    private final MonitorAggregateService aggregateService;

    /**
     * 每 5 分钟重算最近 6 个 5 分钟桶
     * <p>
     * 窗口必须对齐到 5 分钟边界：删除区间是 [start, end)，聚合时首尾样本会被截断成同一个桶，
     * 若 start 未对齐，会生成一条 bucket_time 落在删除区间之外的记录，撞上主键导致整次聚合失败。
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void rollup5m() {
        try {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime end = now.withMinute(now.getMinute() / 5 * 5);
            aggregateService.rebuild5m(end.minusMinutes(30), end);
        } catch (Exception e) {
            log.error("5 分钟聚合失败", e);
        }
    }

    /**
     * 每小时重算最近 6 个 1 小时桶
     */
    @Scheduled(cron = "0 2 * * * *")
    public void rollup1h() {
        try {
            LocalDateTime end = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
            aggregateService.rebuild1h(end.minusHours(6), end);
        } catch (Exception e) {
            log.error("1 小时聚合失败", e);
        }
    }

    /**
     * 每小时清理过期数据
     */
    @Scheduled(cron = "0 10 * * * *")
    public void purge() {
        LocalDateTime now = LocalDateTime.now();
        try {
            int sample = aggregateService.purgeSample(now.minusDays(3), PURGE_BATCH);
            int rollup5m = aggregateService.purgeRollup("5m", now.minusDays(30), PURGE_BATCH);
            int rollup1h = aggregateService.purgeRollup("1h", now.minusDays(365), PURGE_BATCH);
            if (sample + rollup5m + rollup1h > 0) {
                log.info("监控数据清理完成：原始 {} 条，5m {} 条，1h {} 条", sample, rollup5m, rollup1h);
            }
        } catch (Exception e) {
            log.error("监控数据清理失败", e);
        }
    }
}
