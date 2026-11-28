package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author cyx
 * @description Redis key 分页查询结果（SCAN 游标 + 内存分页）
 * @date 2026-11-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Redis key分页结果")
public class RedisKeyListVO {

    @Schema(description = "当前页数据")
    private List<RedisKeyVO> records;

    @Schema(description = "匹配到的总数")
    private Long total;

    @Schema(description = "是否因达到单次扫描上限而被截断")
    private Boolean truncated;

    @Schema(description = "本次实际扫描的 key 数量")
    private Long scanned;
}
