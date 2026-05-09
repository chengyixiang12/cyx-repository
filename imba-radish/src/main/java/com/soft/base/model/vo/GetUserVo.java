package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/12/1 18:49
 **/
@Data
@Schema(description = "获取用户响应参数")
public class GetUserVo {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "部门id")
    private String deptId;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "角色id")
    private List<String> roleIds;
}
