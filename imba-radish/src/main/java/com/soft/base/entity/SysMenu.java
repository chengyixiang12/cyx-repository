package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单信息表
 * @TableName sys_menu
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_menu")
@Data
public class SysMenu extends BaseEntity {

    /**
     * 父菜单ID，顶级菜单的父ID通常为0
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 菜单名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 前端路由路径
     */
    @TableField(value = "path")
    private String path;

    /**
     * 前端组件路径
     */
    @TableField(value = "component")
    private String component;

    /**
     * 菜单图标（如字体图标类名）
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 菜单类型：0-目录，1-菜单，2-按钮
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 排序号，数字越小，排序越靠前
     */
    @TableField(value = "order_num")
    private Integer orderNum;

    /**
     * 菜单状态：0-禁用，1-启用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否显示：0-隐藏，1-显示
     */
    @TableField(value = "visible")
    private Integer visible;

    /**
     * 备注信息
     */
    @TableField(value = "remark")
    private String remark;
}