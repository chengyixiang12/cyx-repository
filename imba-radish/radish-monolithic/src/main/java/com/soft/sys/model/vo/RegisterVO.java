package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "注册")
@Alias(value = "RegisterVO")
public class RegisterVO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}
