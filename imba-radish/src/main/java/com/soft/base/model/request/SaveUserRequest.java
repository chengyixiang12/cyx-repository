package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/18 17:24
 **/
@Data
@Schema(description = "添加用户请求参数")
@Alias(value = "SaveUserRequest")
public class SaveUserRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "部门")
    private Long deptId;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "角色")
    private List<Long> roleIds;
}
