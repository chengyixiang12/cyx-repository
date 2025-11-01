package com.soft.base.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 *
 * @TableName users
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value ="sys_user")
@Alias(value = "SysUser")
public class SysUser extends BaseEntity {


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
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 用户头像
     */
    @TableField(value = "avatar")
    private Long avatar;

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