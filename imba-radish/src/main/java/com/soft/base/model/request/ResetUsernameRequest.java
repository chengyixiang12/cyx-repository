package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long id;

    @Schema(description = "旧用户名")
    private String oldUsername;

    @Schema(description = "新用户名")
    private String newUsername;
}
