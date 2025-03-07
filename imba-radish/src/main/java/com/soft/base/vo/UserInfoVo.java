package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/28 11:10
 **/
@Data
@Schema(description = "获取用户信息响应参数")
public class UserInfoVo {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "部门id")
    private Long deptId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "权限")
    private List<String> permissions;
}
