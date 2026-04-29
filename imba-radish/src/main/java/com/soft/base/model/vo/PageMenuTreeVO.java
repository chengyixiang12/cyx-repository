package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author cyx
 * @date 2026-04-28
 */
@Data
@Schema(description = "菜单树结构分页")
public class PageMenuTreeVO {

    @Schema(description = "唯一标识")
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "路由")
    private String path;

    @Schema(description = "组件")
    private String component;

    @Schema(description = "排序")
    private String orderNum;

    @Schema(description = "状态；1：启用；0：禁用")
    private String status;

    @Schema(description = "是否展示；1：展示；0：隐藏")
    private String visible;

    @Schema(description = "是否有子集")
    private boolean hasChild;
}
