package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description:
 * @date 2026-05-15
 */
@Data
@Schema(description = "获取最新的监控指标")
public class GetLatestActuatorMetricVO {

    @Schema(description = "cpu总数")
    private Integer cpuCount;

    @Schema(description = "cpu使用率")
    private Double cpuUsage;

    @Schema(description = "jvm总内存使用率")
    private Long memoryUsed;

    @Schema(description = "jvm总内存大小")
    private Long memoryMax;

    @Schema(description = "系统运行时间")
    private Long uptime;

    @Schema(description = "磁盘空闲空间")
    private Long diskFree;

    @Schema(description = "磁盘总量")
    private Long diskTotal;
}
