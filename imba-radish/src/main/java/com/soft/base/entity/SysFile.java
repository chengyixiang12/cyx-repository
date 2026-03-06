package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @TableName sys_file
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file")
@Data
public class SysFile extends BaseEntity {

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
    private Integer location;

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
}