package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "注册")
@Alias(value = "RegisterVo")
public class RegisterVo {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}
