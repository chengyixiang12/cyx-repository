package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 19:58
 **/
@Data
@Schema(description = "添加权限请求参数")
public class SavePermissionRequest {

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限类型；1：菜单；2：按钮")
    private String type;

    @Schema(description = "描述")
    private String description;
}
