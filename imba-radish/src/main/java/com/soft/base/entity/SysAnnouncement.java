package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告表
 * @TableName sys_announcement
 */
@TableName(value ="sys_announcement")
@Data
public class SysAnnouncement implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private Long updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除；1：存在；0：删除
     */
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

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
    private String status;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}