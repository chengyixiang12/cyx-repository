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
 * @TableName sys_secret_key
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_secret_key")
@Data
public class SysSecretKey extends BaseEntity {

    /**
     * 公钥
     */
    @TableField(value = "public_key")
    private String publicKey;

    /**
     * 私钥
     */
    @TableField(value = "private_key")
    private String privateKey;

    /**
     * 类型
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;
}