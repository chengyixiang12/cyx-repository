package com.soft.sys.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 趋势查询的行数据；原始表和聚合表用同一套别名映射到该对象
 * @date 2026-11-05
 */
@Data
@Schema(description = "监控趋势行数据")
public class MetricRowDTO {

    @Schema(description = "时间")
    private LocalDateTime collectTime;

    @Schema(description = "宿主机cpu使用率")
    private Double cpuUsage;

    @Schema(description = "进程cpu使用率")
    private Double processCpuUsage;

    @Schema(description = "堆已用")
    private Long heapUsed;

    @Schema(description = "堆上限")
    private Long heapMax;

    @Schema(description = "非堆已用")
    private Long nonheapUsed;

    @Schema(description = "活跃线程")
    private Integer threadsLive;

    @Schema(description = "阻塞线程")
    private Integer threadsBlocked;

    @Schema(description = "gc次数")
    private Integer gcCount;

    @Schema(description = "gc停顿毫秒")
    private Integer gcPauseMs;

    @Schema(description = "qps")
    private Double qps;

    @Schema(description = "p95延迟毫秒")
    private Integer p95Ms;

    @Schema(description = "5xx占比")
    private Double errorRate;

    @Schema(description = "连接池活跃")
    private Integer poolActive;

    @Schema(description = "连接池等待")
    private Integer poolPending;

    @Schema(description = "连接池上限")
    private Integer poolMax;

    @Schema(description = "磁盘可用")
    private Long diskFree;
}
