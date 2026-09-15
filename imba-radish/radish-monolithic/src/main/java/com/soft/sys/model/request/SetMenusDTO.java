package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 10:34
 **/
@Data
@Schema(description = "赋予菜单请求参数")
public class SetMenusDTO {

    @Schema(description = "角色id")
    @NotNull(message = "角色主键不能为空")
    private Long roleId;

    @Schema(description = "菜单id集合")
    private List<Long> menuIds;
}
