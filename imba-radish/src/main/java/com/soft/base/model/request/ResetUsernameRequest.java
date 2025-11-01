package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/13 11:19
 **/

@Data
@Schema(description = "重置用户名请求参数")
public class ResetUsernameRequest {

    @Schema(description = "用户主键")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
}
