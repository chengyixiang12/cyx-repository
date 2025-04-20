package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "编辑角色")
@Alias(value = "EditRoleRequest")
public class EditRoleRequest {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态；1：启用，0：禁用")
    private Integer status;

    @Schema(description = "默认角色；1：是，0：不是")
    private Integer isDefault;

    @Schema(description = "固定角色；1：是，0：不是")
    private Integer fixRole;
}
