package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 组件状态变更记录（只在状态变化时追加一条）
 * @date 2026-11-05
 */
@Data
@TableName(value = "sys_component_health_log")
public class SysComponentHealthLog {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 组件标识
     */
    private String component;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 变化后的状态
     */
    private String status;

    /**
     * 状态说明
     */
    private String detail;

    /**
     * 变化时间
     */
    private LocalDateTime changeTime;
}
