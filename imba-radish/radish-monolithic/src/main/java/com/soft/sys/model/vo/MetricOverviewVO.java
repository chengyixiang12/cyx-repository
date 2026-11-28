package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 监控实时总览（读内存快照，不查库）
 * @date 2026-11-05
 */
@Data
@Schema(description = "监控实时总览")
public class MetricOverviewVO {

    // ========== 静态信息（启动时采集一次） ==========

    @Schema(description = "cpu核数")
    private Integer cpuCount;

    @Schema(description = "堆上限字节")
    private Long heapMax;

    @Schema(description = "磁盘总量字节")
    private Long diskTotal;

    @Schema(description = "jvm版本")
    private String jvmVersion;

    @Schema(description = "操作系统")
    private String osName;

    // ========== 动态信息（每 30 秒刷新） ==========

    @Schema(description = "宿主机cpu使用率(%)")
    private Double cpuUsage;

    @Schema(description = "应用进程cpu使用率(%)")
    private Double processCpuUsage;

    @Schema(description = "堆已用字节")
    private Long heapUsed;

    @Schema(description = "堆使用率(%)")
    private Double heapUsage;

    @Schema(description = "非堆已用字节")
    private Long nonheapUsed;

    @Schema(description = "活跃线程数")
    private Integer threadsLive;

    @Schema(description = "阻塞线程数")
    private Integer threadsBlocked;

    @Schema(description = "最近一个窗口的gc次数")
    private Integer gcCount;

    @Schema(description = "最近一个窗口的gc累计停顿(毫秒)")
    private Integer gcPauseMs;

    @Schema(description = "最近一个窗口的qps")
    private Double qps;

    @Schema(description = "最近一个窗口的p95延迟(毫秒)")
    private Integer p95Ms;

    @Schema(description = "最近一个窗口的5xx占比(%)")
    private Double errorRate;

    @Schema(description = "连接池活跃连接")
    private Integer poolActive;

    @Schema(description = "连接池等待连接")
    private Integer poolPending;

    @Schema(description = "连接池上限")
    private Integer poolMax;

    @Schema(description = "磁盘可用字节")
    private Long diskFree;

    @Schema(description = "运行时长(秒)")
    private Long uptime;

    @Schema(description = "内存中样本的采集时间")
    private LocalDateTime sampleTime;
}
