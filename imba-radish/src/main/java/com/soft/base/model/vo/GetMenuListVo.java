package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/4 23:10
 **/

@Data
@Schema(description = "获取菜单列表响应参数")
@Alias(value = "GetMenuListVo")
public class GetMenuListVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单类型")
    private String type;

    @Schema(description = "菜单状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer orderNum;

    @Schema(description = "图标")
    private String icon;
}
