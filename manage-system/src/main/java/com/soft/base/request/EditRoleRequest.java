package com.soft.base.request;

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

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态；1：启用；0：禁用")
    private Integer status;
}
