package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description Redis key 值详情
 * @date 2026-11-27
 */
@Data
@Schema(description = "Redis key值详情")
public class RedisKeyValueVO {

    @Schema(description = "key名称")
    private String key;

    @Schema(description = "数据类型")
    private String type;

    @Schema(description = "剩余存活时间（秒）；-1 永不过期，-2 已过期或不存在")
    private Long ttlSeconds;

    @Schema(description = "是否已脱敏（敏感key不返回值）")
    private Boolean masked;

    @Schema(description = "脱敏原因")
    private String maskReason;

    @Schema(description = "值内容；hash/list/set/zset 为集合结构，string 为原始值")
    private Object value;

    @Schema(description = "集合类值的元素数量（string 为 0）")
    private Long entryCount;

    @Schema(description = "集合类值是否因超出展示上限而被截断")
    private Boolean truncated;

    @Schema(description = "是否解析失败（如 JDK 序列化的二进制缓存）")
    private Boolean parseFailed;
}
