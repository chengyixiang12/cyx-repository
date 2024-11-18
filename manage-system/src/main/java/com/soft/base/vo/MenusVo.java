package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 程益祥
 * @Description: TODO
 * @DateTime: 2024/11/10 13:47
 **/
@Data
@Schema(description = "获取菜单响应参数")
@Alias(value = "MenusVo")
public class MenusVo {

    @Schema(description = "菜单唯一标识")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单路由路径")
    private String path;

    @Schema(description = "父菜单 ID")
    private Long parentId;

    @Schema(description = "子菜单列表")
    private List<MenusVo> children = new ArrayList<>();
}
