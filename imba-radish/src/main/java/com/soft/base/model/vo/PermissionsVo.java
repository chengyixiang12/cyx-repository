package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 14:43
 **/
@Data
@Schema(description = "获取权限列表响应参数")
public class PermissionsVo {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "状态；1：启用；0：禁用")
    private String status;

    @Schema(description = "描述")
    private String description;
}
