package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告表
 * @TableName sys_announcement
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_announcement")
@Data
public class SysAnnouncement extends BaseEntity {


    /**
     * 公告标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 公布时间
     */
    @TableField(value = "publish_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 公告内容
     */
    @TableField(value = "context")
    private String context;

    /**
     * 状态；1：发布；0：草稿
     */
    @TableField(value = "status")
    private Integer status;
}