package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 忘记密码请求参数
 * @DateTime: 2026/11/5 16:50
 **/
@Schema(description = "忘记密码请求参数")
@Data
public class ForgotPasswordDTO {

    @Schema(description = "注册邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(description = "邮箱验证码")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    @Schema(description = "新密码（RSA 加密后的密文）")
    @NotBlank(message = "密码不能为空")
    private String password;
}
