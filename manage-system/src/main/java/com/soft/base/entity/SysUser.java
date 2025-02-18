package com.soft.base.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 *
 * @TableName users
 */
@Data
@TableName(value ="sys_user")
public class SysUser implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户是否被启用；1：启用；0：禁用
     */
    @TableField(value = "enabled")
    private Boolean enabled;

    /**
     * 账户是否被锁定；1：正常；0：锁定
     */
    @TableField(value = "account_non_locked")
    private Boolean accountNonLocked;

    /**
     * 凭证是否过期；1：正常；0：过期
     */
    @TableField(value = "credentials_non_expired")
    private Boolean credentialsNonExpired;

    /**
     * 账户是否过期；1：正常；0：过期
     */
    @TableField(value = "account_non_expired")
    private Boolean accountNonExpired;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除；1：存在；0：不存在
     */
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

    /**
     * 修改人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    private Long deptId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 设置默认值
     */
    public void setDefault() {
        this.enabled = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
    }
}