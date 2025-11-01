package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能对话详情表
 * @TableName sys_dialogue_details
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_dialogue_details")
@Data
public class SysDialogueDetails extends BaseEntity {

    /**
     * 表sys_dialogue_history的id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 标识；1：用户；0：AI
     */
    @TableField(value = "tag")
    private Integer tag;

    /**
     * 对话内容
     */
    @TableField(value = "content")
    private String content;
}