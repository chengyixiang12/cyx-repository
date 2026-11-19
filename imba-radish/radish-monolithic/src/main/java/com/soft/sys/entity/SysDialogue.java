package com.soft.sys.entity;

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
    private Integer sortOrder;

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

    /**
     * 历史上下文摘要（被挤出保留窗口的旧消息压缩结果）
     */
    @TableField(value = "context_summary")
    private String contextSummary;

    /**
     * 摘要已覆盖到的最后一条对话详情ID
     */
    @TableField(value = "summary_up_to_id")
    private Long summaryUpToId;

    /**
     * 摘要版本号，每次更新摘要自增
     */
    @TableField(value = "summary_version")
    private Integer summaryVersion;
}