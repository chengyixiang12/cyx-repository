package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/22 22:58
 **/

@Data
@Schema(description = "获取角色的下拉框数据响应参数")
@Alias(value = "GetRoleSelectVo")
public class GetRoleSelectVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "名称")
    private String name;
}
