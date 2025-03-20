package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/1 19:02
 **/

@Data
@Schema(description = "获取用户角色传输参数")
public class GetUserRoleDto {

    @Schema(description = "主键")
    private String value;

    @Schema(description = "名称")
    private String label;
}
