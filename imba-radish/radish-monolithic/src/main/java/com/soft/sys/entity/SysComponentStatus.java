package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 组件当前状态（原地更新，最多几行）
 * @date 2026-11-05
 */
@Data
@TableName(value = "sys_component_status")
public class SysComponentStatus {

    /**
     * 组件标识；db / redis / rabbit / minio / websocket / diskSpace
     */
    @TableId(value = "component", type = IdType.INPUT)
    private String component;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 状态；UP / DOWN / DISABLED / UNKNOWN
     */
    private String status;

    /**
     * 状态说明
     */
    private String detail;

    /**
     * 进入当前状态的时间
     */
    private LocalDateTime sinceTime;

    /**
     * 最后探测时间
     */
    private LocalDateTime lastCheckTime;
}
