package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName sys_dict_data
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_dict_data")
@Data
public class SysDictData extends BaseEntity {

    /**
     * sys_dict_type的主键
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 排序字段
     */
    @TableField(value = "sort_order")
    private Long sortOrder;

    /**
     * 标签
     */
    @TableField(value = "label")
    private String label;

    /**
     * 键值
     */
    @TableField(value = "value")
    private String value;

    /**
     * 样式属性（其他样式扩展）
     */
    @TableField(value = "css_class")
    private String cssClass;

    /**
     * 表格回显样式
     */
    @TableField(value = "list_class")
    private String listClass;

    /**
     * 是否默认；1：是 0：否
     */
    @TableField(value = "is_default")
    private Integer isDefault;

    /**
     * 状态；1：启用；0：停用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;
}