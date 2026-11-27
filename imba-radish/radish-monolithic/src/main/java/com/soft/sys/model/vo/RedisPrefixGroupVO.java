package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cyx
 * @description Redis key 前缀分组统计
 * @date 2026-11-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Redis前缀分组")
public class RedisPrefixGroupVO {

    @Schema(description = "key前缀")
    private String prefix;

    @Schema(description = "业务含义")
    private String businessName;

    @Schema(description = "key数量")
    private Long keyCount;

    @Schema(description = "是否为敏感前缀（查看值时会脱敏）")
    private Boolean sensitive;
}
