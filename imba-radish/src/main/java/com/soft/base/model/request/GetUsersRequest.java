package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/1 16:34
 **/

@EqualsAndHashCode(callSuper = false)
@Schema(description = "获取所有用户请求参数")
@Data
public class GetUsersRequest extends PageRequest {

    @Schema(description = "部门id")
    private Long deptId;

    @Schema(description = "关键字检索")
    private String nameLikeQry;

    @Schema(description = "用户是否被启用；1：启用；0：禁用")
    private Integer enabled;

    @Schema(description = "账户是否被锁定；1：正常；0：锁定")
    private Integer accountNonLocked;
}
