package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/18 17:51
 **/

@Data
@Schema(description = "编辑用户请求参数")
@Alias(value = "EditUserRequest")
public class EditUserRequest {

    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "部门")
    @NotNull(message = "部门不能为空")
    private Long deptId;

    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "角色")
    private List<Long> roleIds;

    @Schema(description = "用户头像")
    private Long avatar;
}
