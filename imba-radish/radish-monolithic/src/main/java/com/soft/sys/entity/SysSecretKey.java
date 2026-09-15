package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * 用途类型；登录、数据传输等
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 密钥类型；AES；RSA
     */
    @TableField(value = "secret_type")
    private String secretType;
}