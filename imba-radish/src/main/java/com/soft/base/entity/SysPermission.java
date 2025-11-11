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
 * @TableName sys_permission
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_permission")
@Data
public class SysPermission extends BaseEntity {

    /**
     * 权限名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 权限编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 状态；1：启用；0：禁用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;
}