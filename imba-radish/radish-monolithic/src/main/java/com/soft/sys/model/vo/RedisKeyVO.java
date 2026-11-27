package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description Redis key 列表项
 * @date 2026-11-27
 */
@Data
@Schema(description = "Redis key列表项")
public class RedisKeyVO {

    @Schema(description = "key名称")
    private String key;

    @Schema(description = "数据类型：string/hash/list/set/zset/none")
    private String type;

    @Schema(description = "剩余存活时间（秒）；-1 永不过期，-2 已过期或不存在")
    private Long ttlSeconds;

    @Schema(description = "是否为敏感key（值不可查看）")
    private Boolean sensitive;
}
