package com.soft.sys.service.impl;

import com.soft.sys.entity.SysComponentHealthLog;
import com.soft.sys.entity.SysComponentStatus;
import com.soft.sys.mapper.SysComponentHealthLogMapper;
import com.soft.sys.mapper.SysComponentStatusMapper;
import com.soft.sys.mapper.SysMetricRollupMapper;
import com.soft.sys.mapper.SysMetricSampleMapper;
import com.soft.sys.model.dto.MetricRowDTO;
import com.soft.sys.model.vo.ComponentOverviewVO;
import com.soft.sys.model.vo.MetricOverviewVO;
import com.soft.sys.model.vo.TrendPointVO;
import com.soft.sys.model.vo.TrendSeriesVO;
import com.soft.sys.monitor.MetricSnapshotHolder;
import com.soft.sys.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author cyx
 * @description 系统监控实现
 * @date 2026-11-05
 */
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    /**
     * 时间范围超过该值时改用 5 分钟聚合数据
     */
    private static final long RAW_MAX_MINUTES = 120;
    /**
     * 时间范围超过该值时改用 1 小时聚合数据
     */
    private static final long ROLLUP_5M_MAX_MINUTES = 2 * 24 * 60;

    private static final String GRANULARITY_5M = "5m";
    private static final String GRANULARITY_1H = "1h";

    private static final String DEFAULT_METRICS = "cpu_usage,heap_usage,qps,p95_ms";

    private final MetricSnapshotHolder snapshotHolder;
    private final SysMetricSampleMapper sampleMapper;
    private final SysMetricRollupMapper rollupMapper;
    private final SysComponentStatusMapper statusMapper;
    private final SysComponentHealthLogMapper healthLogMapper;

    @Override
    public MetricOverviewVO getOverview() {
        return snapshotHolder.current();
    }

    @Override
    public List<TrendSeriesVO> getTrend(String range, String metrics) {
        TimeWindow window = TimeWindow.parse(range);
        List<MetricRowDTO> rows = queryRows(window);

        Map<String, MetricDef> requested = resolveMetrics(metrics);
        List<TrendSeriesVO> series = new ArrayList<>(requested.size());
        for (MetricDef def : requested.values()) {
            TrendSeriesVO vo = new TrendSeriesVO();
            vo.setKey(def.key());
            vo.setName(def.name());
            vo.setUnit(def.unit());
            List<TrendPointVO> points = new ArrayList<>(rows.size());
            for (MetricRowDTO row : rows) {
                Double value = def.extractor().apply(row);
                if (value == null) {
                    continue;
                }
                points.add(new TrendPointVO(toEpochMillis(row.getCollectTime()), value));
            }
            vo.setPoints(points);
            series.add(vo);
        }
        return series;
    }

    @Override
    public ComponentOverviewVO getComponents() {
        ComponentOverviewVO vo = new ComponentOverviewVO();
        List<ComponentOverviewVO.ComponentStatusVO> components = new ArrayList<>();
        for (SysComponentStatus status : statusMapper.selectAllOrdered()) {
            ComponentOverviewVO.ComponentStatusVO item = new ComponentOverviewVO.ComponentStatusVO();
            item.setComponent(status.getComponent());
            item.setComponentName(status.getComponentName());
            item.setStatus(status.getStatus());
            item.setDetail(status.getDetail());
            item.setSinceTime(status.getSinceTime());
            item.setLastCheckTime(status.getLastCheckTime());
            components.add(item);
        }
        vo.setComponents(components);

        List<ComponentOverviewVO.HealthChangeVO> changes = new ArrayList<>();
        for (SysComponentHealthLog change : healthLogMapper.selectRecent(20)) {
            ComponentOverviewVO.HealthChangeVO item = new ComponentOverviewVO.HealthChangeVO();
            item.setComponentName(change.getComponentName());
            item.setStatus(change.getStatus());
            item.setDetail(change.getDetail());
            item.setChangeTime(change.getChangeTime());
            changes.add(item);
        }
        vo.setChanges(changes);
        return vo;
    }

    /**
     * 按时间范围选择数据源：短范围看原始样本，长范围看聚合数据
     */
    private List<MetricRowDTO> queryRows(TimeWindow window) {
        long minutes = window.minutes();
        if (minutes <= RAW_MAX_MINUTES) {
            return sampleMapper.selectRawRange(window.start(), window.end());
        }
        if (minutes <= ROLLUP_5M_MAX_MINUTES) {
            return rollupMapper.selectRollupRange(GRANULARITY_5M, window.start(), window.end());
        }
        return rollupMapper.selectRollupRange(GRANULARITY_1H, window.start(), window.end());
    }

    private Map<String, MetricDef> resolveMetrics(String metrics) {
        String keys = (metrics == null || metrics.isBlank()) ? DEFAULT_METRICS : metrics;
        Map<String, MetricDef> result = new LinkedHashMap<>();
        for (String key : keys.split(",")) {
            MetricDef def = METRICS.get(key.trim());
            if (def != null) {
                result.put(def.key(), def);
            }
        }
        return result;
    }

    private long toEpochMillis(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 可查询的指标定义；新增指标只需要在这里加一行
     */
    private record MetricDef(String key, String name, String unit, Function<MetricRowDTO, Double> extractor) {
    }

    private static final Map<String, MetricDef> METRICS = buildMetrics();

    private static Map<String, MetricDef> buildMetrics() {
        Map<String, MetricDef> map = new LinkedHashMap<>();
        put(map, new MetricDef("cpu_usage", "宿主机 CPU", "%", row -> row.getCpuUsage()));
        put(map, new MetricDef("process_cpu_usage", "应用 CPU", "%", row -> row.getProcessCpuUsage()));
        put(map, new MetricDef("heap_usage", "堆使用率", "%", MonitorServiceImpl::heapUsage));
        put(map, new MetricDef("heap_used", "堆已用", "MB", row -> toMb(row.getHeapUsed())));
        put(map, new MetricDef("nonheap_used", "非堆已用", "MB", row -> toMb(row.getNonheapUsed())));
        put(map, new MetricDef("threads_live", "活跃线程", "个",
                row -> row.getThreadsLive() == null ? null : row.getThreadsLive().doubleValue()));
        put(map, new MetricDef("gc_pause_ms", "GC 停顿", "ms",
                row -> row.getGcPauseMs() == null ? null : row.getGcPauseMs().doubleValue()));
        put(map, new MetricDef("qps", "QPS", "次/秒", row -> row.getQps()));
        put(map, new MetricDef("p95_ms", "P95 延迟", "ms",
                row -> row.getP95Ms() == null ? null : row.getP95Ms().doubleValue()));
        put(map, new MetricDef("error_rate", "5xx 占比", "%", row -> row.getErrorRate()));
        put(map, new MetricDef("pool_pending", "连接池等待", "个",
                row -> row.getPoolPending() == null ? null : row.getPoolPending().doubleValue()));
        put(map, new MetricDef("disk_free", "磁盘可用", "GB", row -> {
            Long free = row.getDiskFree();
            return free == null ? null : Math.round(free * 100D / 1073741824D) / 100D;
        }));
        return map;
    }

    private static void put(Map<String, MetricDef> map, MetricDef def) {
        map.put(def.key(), def);
    }

    private static Double heapUsage(MetricRowDTO row) {
        if (row.getHeapUsed() == null || row.getHeapMax() == null || row.getHeapMax() <= 0) {
            return null;
        }
        return Math.round(row.getHeapUsed() * 10000D / row.getHeapMax()) / 100D;
    }

    private static Double toMb(Long bytes) {
        return bytes == null ? null : Math.round(bytes / 1024D / 1024D * 100D) / 100D;
    }

    /**
     * 时间范围解析；非法值按 15 分钟处理
     */
    private record TimeWindow(LocalDateTime start, LocalDateTime end) {

        private static final Map<String, Long> SUPPORTED = Map.of(
                "15m", 15L, "1h", 60L, "6h", 360L, "1d", 1440L, "7d", 10080L, "30d", 43200L);

        private static TimeWindow parse(String range) {
            Long minutes = SUPPORTED.get(range == null ? "" : range.trim());
            long value = minutes == null ? 15L : minutes;
            LocalDateTime end = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            return new TimeWindow(end.minusMinutes(value), end);
        }

        private long minutes() {
            return ChronoUnit.MINUTES.between(start, end);
        }
    }
}
