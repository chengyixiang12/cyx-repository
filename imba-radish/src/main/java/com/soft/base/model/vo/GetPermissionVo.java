package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyq
 * @date 2025/11/16
 * @description
 */
@Data
@Schema(description = "获取权限响应参数")
public class GetPermissionVo {

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限类型")
    private String type;

    @Schema(description = "权限状态")
    private Integer status;

    @Schema(description = "描述")
    private String description;
}
