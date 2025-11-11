package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyq
 * @date 2025/11/4
 * @description
 */
@Data
@Schema(description = "编辑权限")
public class EditPermissionRequest {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "描述")
    private String description;
}
