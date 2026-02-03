package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName sys_dept
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_dept")
@Data
public class SysDept extends BaseEntity {
    /**
     * 父级id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 排序字段
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 部门编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 部门名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 层级
     */
    @TableField(value = "level")
    private String level;
}