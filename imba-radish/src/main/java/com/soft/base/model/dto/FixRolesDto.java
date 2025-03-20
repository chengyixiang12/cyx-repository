package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 9:37
 **/

@Data
@Schema(description = "固定角色传输参数")
public class FixRolesDto {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "固定角色")
    private Integer fixRole;

    @Schema(description = "默认角色")
    private Integer isDefault;
}
