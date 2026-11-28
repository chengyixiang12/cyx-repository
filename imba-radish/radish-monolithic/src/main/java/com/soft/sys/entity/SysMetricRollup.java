package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 监控指标聚合数据
 * <p>
 * 主键是 (granularity, bucket_time)，由 SQL 直接写入，不走 MyBatis-Plus 的雪花 id。
 * @date 2026-11-05
 */
@Data
@TableName(value = "sys_metric_rollup")
public class SysMetricRollup {

    /**
     * 粒度；5m：5 分钟；1h：1 小时
     */
    private String granularity;

    /**
     * 聚合桶起始时间
     */
    private LocalDateTime bucketTime;

    private Double cpuUsageAvg;

    private Double processCpuUsageAvg;

    private Long heapUsedAvg;

    private Long heapUsedMax;

    private Long heapMaxAvg;

    private Long nonheapUsedAvg;

    private Integer threadsLiveAvg;

    private Integer threadsLiveMax;

    private Integer gcCountSum;

    private Integer gcPauseMsSum;

    private Double qpsAvg;

    private Double qpsMax;

    private Integer p95MsMax;

    private Double errorRateAvg;

    private Double errorRateMax;

    private Integer poolActiveMax;

    private Integer poolPendingMax;

    private Integer poolMaxAvg;

    private Long diskFreeMin;
}
