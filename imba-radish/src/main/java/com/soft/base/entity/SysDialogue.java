package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能对话表
 * @TableName sys_dialogue
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_dialogue")
@Data
public class SysDialogue extends BaseEntity {

    /**
     * 排序字段
     */
    @TableField(value = "sort_order")
    private Long sortOrder;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 置顶标识；1：置顶；0：不置顶
     */
    @TableField(value = "top_flag")
    private Integer topFlag;
}