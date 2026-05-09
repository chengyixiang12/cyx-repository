package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "修改个人信息请求参数")
public class EditSelfRequest {

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

    @Schema(description = "用户头像")
    private Long avatar;
}
