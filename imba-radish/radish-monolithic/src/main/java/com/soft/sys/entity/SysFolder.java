package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyx
 * @description 文件文件夹（树形：parent_id 为 NULL 表示根目录）
 * @date 2026-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_folder")
public class SysFolder extends BaseEntity {

    /**
     * 文件夹名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 父文件夹主键；NULL：根目录
     */
    @TableField(value = "parent_id")
    private Long parentId;
}
