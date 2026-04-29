package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyx
 * @description:
 * @date 2026-04-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageMenuTreeRequest extends PageRequest {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "菜单状态")
    private String status;

    @Schema(description = "菜单类型")
    private String type;

    @Schema(description = "父级")
    private Long parentId;
}
