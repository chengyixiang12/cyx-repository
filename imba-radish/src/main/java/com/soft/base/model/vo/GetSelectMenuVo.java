package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/19 23:07
 **/

@Data
@Schema(description = "获取下拉菜单结构")
@Alias(value = "GetSelectMenuVo")
public class GetSelectMenuVo {

    @Schema(description = "菜单主键")
    private Integer id;

    @Schema(description = "菜单名称")
    private String name;
}
