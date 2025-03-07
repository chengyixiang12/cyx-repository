package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 10:58
 **/
@Data
@Schema(description = "赋予权限请求参数")
public class SetPermissionsRequest {

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "权限id集合")
    private List<Long> permissionIds;
}
