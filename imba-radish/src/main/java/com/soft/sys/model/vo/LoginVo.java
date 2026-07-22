package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "登录")
@Alias(value = "LoginVO")
public class LoginVO {

    @Schema(description = "用户认证")
    private String token;

    @Schema(description = "用户名")
    private String username;
}
