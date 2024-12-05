package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "新增角色")
@Alias(value = "SaveRoleRequest")
public class SaveRoleRequest {

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "角色编码（以ROLE_开头）", example = "ROLE_ADMIN")
    private String code;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态；1：启用；0：禁用")
    private Integer status;
}
