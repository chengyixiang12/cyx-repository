package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/6 13:40
 **/

@Data
@Schema(description = "获取菜单（单）相应参数")
@Alias(value = "GetMenuVo")
public class GetMenuVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "父级主键")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "前端路由路径")
    private String path;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "菜单图标（如字体图标类名）")
    private String icon;

    @Schema(description = "菜单类型：0-目录，1-菜单，2-按钮")
    private String type;

    @Schema(description = "排序号，数字越小，排序越靠前")
    private Integer orderNum;

    @Schema(description = "菜单状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "是否显示：0-隐藏，1-显示")
    private Integer visible;

    @Schema(description = "备注信息")
    private String remark;
}
