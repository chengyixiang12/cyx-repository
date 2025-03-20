package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "登录请求参数")
@Alias(value = "LoginRequest")
public class LoginRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "登录方式，password：密码登录；email：邮箱验证码登录")
    private String loginMethod;

    @Schema(description = "图形验证码")
    private String graphicsCaptcha;
}
