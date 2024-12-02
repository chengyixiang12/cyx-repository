package com.soft.base.vo;

import com.soft.base.dto.GetUserDeptDto;
import com.soft.base.dto.GetUserRoleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/1 18:49
 **/
@Data
@Schema(description = "获取用户响应参数")
public class GetUserVo {

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

    private GetUserDeptDto getUserDeptDto;

    private List<GetUserRoleDto> getUserRoleDtoList;
}
