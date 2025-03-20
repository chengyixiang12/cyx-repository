package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/7 17:43
 **/

@Data
@Schema(description = "用户使用邮箱登录")
public class UserEmailDto {

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户是否被启用；1：启用；0：禁用")
    private Boolean enabled;

    @Schema(description = "账户是否被锁定；1：正常；0：锁定")
    private Boolean accountNonLocked;

    @Schema(description = "凭证是否过期；1：正常；0：过期")
    private Boolean credentialsNonExpired;

    @Schema(description = "账户是否过期；1：正常；0：过期")
    private Boolean accountNonExpired;
}
