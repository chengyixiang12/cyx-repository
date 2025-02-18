package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户角色表
 * @TableName sys_user_role
 */
@TableName(value ="sys_user_role")
@Data
public class SysUserRole implements Serializable {
    /**
     * 角色主键
     */
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    private Long userId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}