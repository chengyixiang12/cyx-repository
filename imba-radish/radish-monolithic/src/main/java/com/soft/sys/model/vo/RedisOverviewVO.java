package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description Redis 总览信息
 * @date 2026-11-27
 */
@Data
@Schema(description = "Redis总览")
public class RedisOverviewVO {

    @Schema(description = "key总数")
    private Long dbSize;

    @Schema(description = "Redis版本")
    private String redisVersion;

    @Schema(description = "已用内存（人类可读，如 1.2M）")
    private String usedMemoryHuman;

    @Schema(description = "已用内存（字节）")
    private Long usedMemoryBytes;

    @Schema(description = "内存上限（人类可读，unlimited 表示无上限）")
    private String maxMemoryHuman;

    @Schema(description = "内存碎片率")
    private Double memFragmentationRatio;

    @Schema(description = "连接客户端数")
    private Long connectedClients;

    @Schema(description = "keyspace命中次数")
    private Long keyspaceHits;

    @Schema(description = "keyspace未命中次数")
    private Long keyspaceMisses;

    @Schema(description = "命中率（0-100，无请求时为 null）")
    private Double hitRate;

    @Schema(description = "已运行天数")
    private Long uptimeDays;
}
