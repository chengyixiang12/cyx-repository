package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/16 19:46
 **/

@Data
@Schema(description = "添加菜单请求参数")
@Alias(value = "SaveMenuRequest")
public class SaveMenuRequest {

    @Schema(description = "父级id，若为根节点则不传")
    private String parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "前端路由路径")
    private String path;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "菜单类型：0-目录，1-菜单，2-按钮")
    private String type;

    @Schema(description = "排序号，数字越小，排序越靠前")
    private Integer orderNum;

    @Schema(description = "是否显示：0-隐藏，1-显示")
    private Integer visible;

    @Schema(description = "备注信息")
    private String remark;
}
