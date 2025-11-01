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
    private Long sortOrder;

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