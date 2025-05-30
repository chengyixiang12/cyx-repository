package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName sys_file
 */
@TableName(value ="sys_file")
@Data
public class SysFile implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
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
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
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
     * 源文件名
     */
    @TableField(value = "original_name")
    private String originalName;

    /**
     * 存储文件名
     */
    @TableField(value = "file_key")
    private String fileKey;

    /**
     * 存储地址；1：minio；2：磁盘
     */
    @TableField(value = "location")
    private String location;

    /**
     * 桶名
     */
    @TableField(value = "bucket")
    private String bucket;

    /**
     * 文件路径
     */
    @TableField(value = "object_key")
    private String objectKey;

    /**
     * 文件后缀；示例：.txt、.jpg
     */
    @TableField(value = "file_suffix")
    private String fileSuffix;

    /**
     * 文件大小；单位：B
     */
    @TableField(value = "file_size")
    private Long fileSize;

    /**
     * 文件hash
     */
    @TableField(value = "file_hash")
    private String fileHash;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}