package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "角色")
@Alias(value = "SysRoleVo")
public class SysRoleVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "排序字段")
    private Integer sortOrder;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态；1：启用；0：禁用")
    private Integer status;

    @Schema(description = "是否标记为系统默认角色；1：是；0：不是")
    private Integer isDefault;
}
