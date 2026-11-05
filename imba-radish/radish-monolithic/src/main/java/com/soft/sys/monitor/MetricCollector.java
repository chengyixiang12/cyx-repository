package com.soft.sys.monitor;

import com.soft.sys.entity.SysMetricSample;
import com.soft.sys.mapper.SysMetricSampleMapper;
import com.soft.sys.model.vo.MetricOverviewVO;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author cyx
 * @description 指标采集器：每 30 秒采集一次资源与应用指标
 * <p>
 * 两个关键点：
 * <ol>
 *     <li>http.server.requests、jvm.gc.pause 是进程启动至今的累积值，
 *     必须与上一拍做差分才能得到“这 30 秒”的 QPS / 错误率 / GC 停顿；</li>
 *     <li>P95 依赖 micrometer 的直方图桶（需开启 percentiles-histogram），
 *     同样是先把桶计数差分，再按 95% 分位落桶。</li>
 * </ol>
 * @date 2026-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricCollector {

    private static final long INTERVAL_MILLIS = 30_000L;

    private final MeterRegistry meterRegistry;
    private final SysMetricSampleMapper sysMetricSampleMapper;
    private final MetricSnapshotHolder snapshotHolder;

    /**
     * 上一拍的请求/GC 累积值，只在本类的调度线程里读写
     */
    private WindowStat prevRequest;
    private WindowStat prevGc;
    private long prevSampleNanos;

    @Scheduled(fixedRate = INTERVAL_MILLIS, initialDelay = 10_000)
    public void collect() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long nowNanos = System.nanoTime();

            SysMetricSample sample = new SysMetricSample();
            sample.setCollectTime(now);

            // ===== 资源指标 =====
            fillResource(sample);

            // ===== 应用指标（窗口差分） =====
            fillApplication(sample, nowNanos);

            // ===== 落库 + 刷新内存快照 =====
            try {
                sysMetricSampleMapper.insert(sample);
            } catch (DuplicateKeyException e) {
                // 重启或时钟回拨导致的重复采样，直接丢弃
                log.debug("采样时间重复，已忽略：{}", now);
            }
            snapshotHolder.update(toOverview(sample));
        } catch (Exception e) {
            // 采集失败不能影响业务线程，也不能让调度任务停止
            log.error("采集监控指标失败", e);
        }
    }

    // ==================== 资源指标 ====================

    private void fillResource(SysMetricSample sample) {
        // Micrometer 的 CPU 指标是 0~1 的比值，统一换算成百分比后再入库
        sample.setCpuUsage(percent("system.cpu.usage"));
        sample.setProcessCpuUsage(percent("process.cpu.usage"));

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        sample.setHeapUsed(heap.getUsed());
        sample.setHeapMax(heap.getMax() > 0 ? heap.getMax() : null);
        sample.setNonheapUsed(nonHeap.getUsed());

        sample.setThreadsLive(toInt(gauge("jvm.threads.live")));
        sample.setThreadsBlocked(toInt(gauge("jvm.threads.states", "state", "blocked")));

        sample.setPoolActive(toInt(gauge("hikaricp.connections.active")));
        sample.setPoolPending(toInt(gauge("hikaricp.connections.pending")));
        sample.setPoolMax(toInt(gauge("hikaricp.connections.max")));

        sample.setDiskFree(diskFree());
    }

    // ==================== 应用指标 ====================

    private void fillApplication(SysMetricSample sample, long nowNanos) {
        WindowStat request = readRequestWindow();
        WindowStat gc = readGcWindow();

        if (prevRequest == null || prevSampleNanos == 0) {
            // 第一拍只建立基线，没有可用的窗口数据
            prevRequest = request;
            prevGc = gc;
            prevSampleNanos = nowNanos;
            sample.setQps(0D);
            sample.setErrorRate(0D);
            sample.setP95Ms(0);
            sample.setGcCount(0);
            sample.setGcPauseMs(0);
            return;
        }

        double elapsedSeconds = Math.max((nowNanos - prevSampleNanos) / 1_000_000_000D, 1D);

        long requestDelta = Math.max(request.count - prevRequest.count, 0);
        long errorDelta = Math.max(request.errors - prevRequest.errors, 0);

        sample.setQps(round2(requestDelta / elapsedSeconds));
        sample.setErrorRate(requestDelta == 0 ? 0D : round2(errorDelta * 100D / requestDelta));
        sample.setP95Ms(p95(request.diffBuckets(prevRequest)));

        sample.setGcCount((int) Math.max(gc.count - prevGc.count, 0));
        sample.setGcPauseMs((int) Math.max(Math.round(gc.totalMs - prevGc.totalMs), 0));

        prevRequest = request;
        prevGc = gc;
        prevSampleNanos = nowNanos;
    }

    /**
     * 读取所有 http.server.requests 计时器的累积值与直方图桶
     */
    private WindowStat readRequestWindow() {
        long count = 0;
        long errors = 0;
        Map<Double, Double> buckets = new HashMap<>();

        for (Timer timer : meterRegistry.find("http.server.requests").timers()) {
            long timerCount = timer.count();
            count += timerCount;
            if ("SERVER_ERROR".equals(timer.getId().getTag("outcome"))) {
                errors += timerCount;
            }
            HistogramSnapshot snapshot = timer.takeSnapshot();
            CountAtBucket[] counts = snapshot.histogramCounts();
            if (counts == null) {
                continue;
            }
            for (CountAtBucket bucket : counts) {
                if (Double.isInfinite(bucket.bucket())) {
                    continue;
                }
                buckets.merge(bucket.bucket(TimeUnit.MILLISECONDS), bucket.count(), Double::sum);
            }
        }
        return new WindowStat(count, errors, 0, buckets);
    }

    /**
     * 读取所有 jvm.gc.pause 计时器的累积次数与总停顿
     */
    private WindowStat readGcWindow() {
        long count = 0;
        double totalMs = 0;
        for (Timer timer : meterRegistry.find("jvm.gc.pause").timers()) {
            count += timer.count();
            totalMs += timer.totalTime(TimeUnit.MILLISECONDS);
        }
        return new WindowStat(count, 0, totalMs, Map.of());
    }

    /**
     * 用窗口内的桶增量计算 P95：累计到 95% 请求落在的那个桶上界
     */
    private int p95(Map<Double, Double> bucketDelta) {
        double total = bucketDelta.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) {
            return 0;
        }
        double target = total * 0.95;
        double cumulative = 0;
        double lastBucket = 0;
        for (Map.Entry<Double, Double> entry : new TreeMap<>(bucketDelta).entrySet()) {
            cumulative += entry.getValue();
            lastBucket = entry.getKey();
            if (cumulative >= target) {
                return (int) Math.ceil(entry.getKey());
            }
        }
        return (int) Math.ceil(lastBucket);
    }

    // ==================== 快照 ====================

    private MetricOverviewVO toOverview(SysMetricSample sample) {
        MetricOverviewVO vo = new MetricOverviewVO();
        vo.setCpuCount(toInt(gauge("system.cpu.count")));
        vo.setHeapMax(sample.getHeapMax());
        vo.setDiskTotal(diskTotal());
        vo.setJvmVersion(System.getProperty("java.version"));
        vo.setOsName(System.getProperty("os.name"));

        vo.setCpuUsage(sample.getCpuUsage());
        vo.setProcessCpuUsage(sample.getProcessCpuUsage());
        vo.setHeapUsed(sample.getHeapUsed());
        if (sample.getHeapMax() != null && sample.getHeapMax() > 0 && sample.getHeapUsed() != null) {
            vo.setHeapUsage(round2(sample.getHeapUsed() * 100D / sample.getHeapMax()));
        }
        vo.setNonheapUsed(sample.getNonheapUsed());
        vo.setThreadsLive(sample.getThreadsLive());
        vo.setThreadsBlocked(sample.getThreadsBlocked());
        vo.setGcCount(sample.getGcCount());
        vo.setGcPauseMs(sample.getGcPauseMs());
        vo.setQps(sample.getQps());
        vo.setP95Ms(sample.getP95Ms());
        vo.setErrorRate(sample.getErrorRate());
        vo.setPoolActive(sample.getPoolActive());
        vo.setPoolPending(sample.getPoolPending());
        vo.setPoolMax(sample.getPoolMax());
        vo.setDiskFree(sample.getDiskFree());
        vo.setUptime(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        vo.setSampleTime(sample.getCollectTime());
        return vo;
    }

    // ==================== 工具方法 ====================

    private Double gauge(String name, String... tags) {
        try {
            var search = meterRegistry.find(name);
            for (int i = 0; i + 1 < tags.length; i += 2) {
                search = search.tag(tags[i], tags[i + 1]);
            }
            Gauge gauge = search.gauge();
            return gauge == null ? null : gauge.value();
        } catch (Exception e) {
            log.debug("读取指标 {} 失败：{}", name, e.getMessage());
            return null;
        }
    }

    private long diskFree() {
        Collection<Gauge> gauges = meterRegistry.find("disk.free").gauges();
        double min = Double.MAX_VALUE;
        for (Gauge gauge : gauges) {
            min = Math.min(min, gauge.value());
        }
        return min == Double.MAX_VALUE ? 0L : (long) min;
    }

    private long diskTotal() {
        double total = 0;
        for (Gauge gauge : meterRegistry.find("disk.total").gauges()) {
            total += gauge.value();
        }
        return (long) total;
    }

    private Double round2(Double value) {
        return value == null ? null : Math.round(value * 100) / 100D;
    }

    /**
     * 读取 0~1 的比值型指标并换算成百分比（如 CPU 使用率）
     */
    private Double percent(String name, String... tags) {
        Double value = gauge(name, tags);
        return value == null ? null : round2(value * 100D);
    }

    private Integer toInt(Double value) {
        return value == null ? null : value.intValue();
    }

    /**
     * 一次快照的累积状态；差分后得到窗口值
     */
    private static final class WindowStat {

        private final long count;
        private final long errors;
        private final double totalMs;
        private final Map<Double, Double> buckets;

        private WindowStat(long count, long errors, double totalMs, Map<Double, Double> buckets) {
            this.count = count;
            this.errors = errors;
            this.totalMs = totalMs;
            this.buckets = buckets;
        }

        /**
         * 计算桶计数的增量；上一拍不存在的桶按 0 处理
         */
        private Map<Double, Double> diffBuckets(WindowStat previous) {
            Map<Double, Double> delta = new HashMap<>();
            for (Map.Entry<Double, Double> entry : this.buckets.entrySet()) {
                double before = previous.buckets.getOrDefault(entry.getKey(), 0D);
                double diff = entry.getValue() - before;
                if (diff > 0) {
                    delta.put(entry.getKey(), diff);
                }
            }
            return delta;
        }
    }
}
