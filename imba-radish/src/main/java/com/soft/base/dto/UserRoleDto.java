package com.soft.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/18 16:53
 **/

@Data
@Schema(description = "用户角色传输参数")
public class UserRoleDto {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "角色id")
    private Long roleId;
}
