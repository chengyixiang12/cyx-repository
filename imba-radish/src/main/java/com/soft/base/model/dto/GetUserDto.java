package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/1 19:14
 **/

@Data
@Schema(description = "获取用户传输参数")
public class GetUserDto {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "部门id")
    private Long deptId;
}
