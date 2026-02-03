package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName sys_dict_type
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_dict_type")
@Data
public class SysDictType extends BaseEntity {

    /**
     * 排序字段
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 字典名称
     */
    @TableField(value = "dict_name")
    private String dictName;

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