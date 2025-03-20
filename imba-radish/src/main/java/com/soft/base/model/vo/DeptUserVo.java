package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 11:51
 **/

@Data
@Schema(description = "组织架构用户")
@Alias(value = "DeptUserVo")
public class DeptUserVo {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "部门id", hidden = true)
    private Long deptId;
}
