package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/25 17:10
 **/

@Data
@Schema(description = "重置密码")
@Alias(value = "ResetPasswordRequest")
public class ResetPasswordRequest {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "密码")
    @NotBlank(message = "新密码不能为空")
    private String password;
}
