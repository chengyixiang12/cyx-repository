package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/18 18:05
 **/

@Data
@Schema(description = "所有用户相应参数")
@Alias(value = "AllUserVo")
public class AllUserVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户是否被启用；1：启用；0：禁用")
    private Integer enabled;

    @Schema(description = "账户是否被锁定；1：正常；0：锁定")
    private Integer accountNonLocked;
}
