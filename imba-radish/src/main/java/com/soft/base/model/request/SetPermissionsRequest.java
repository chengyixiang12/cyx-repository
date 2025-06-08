package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description = "角色主键")
    @NotNull(message = "角色主键不能为空")
    private Long roleId;

    @Schema(description = "权限id集合")
    private List<Long> permissionIds;
}
