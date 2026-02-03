package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 * @TableName sys_role
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_role")
@Data
public class SysRole extends BaseEntity {


    /**
     * 排序字段
     */
    @TableField(value = "sort_order")
    private Long sortOrder;

    /**
     * 角色编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 角色名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 状态；1：启用；0：禁用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否标记为系统默认角色；1：是；0：不是
     */
    @TableField(value = "is_default")
    private Integer isDefault;

    /**
     * 是否为固定角色（固定角色无法被删除）；1：是；0：不是
     */
    @TableField(value = "fix_role")
    private Integer fixRole;
}