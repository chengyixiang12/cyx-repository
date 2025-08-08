package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 调度任务表
 * @TableName sys_schedule_job
 */
@TableName(value ="sys_schedule_job")
@Data
public class SysScheduleJob {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除；1：存在；0：删除
     */
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    private String delFlag;

    /**
     * 任务名称
     */
    @TableField(value = "job_name")
    private String jobName;

    /**
     * 任务组
     */
    @TableField(value = "job_group")
    private String jobGroup;

    /**
     * cron表达式
     */
    @TableField(value = "cron")
    private String cron;

    /**
     * 任务类型
     */
    @TableField(value = "job_type")
    private String jobType;

    /**
     * 任务状态；1：启用；0：暂停
     */
    @TableField(value = "status")
    private String status;

    /**
     * 任务所需参数
     */
    @TableField(value = "job_param")
    private String jobParam;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 间隔
     */
    @TableField(value = "job_interval")
    private Integer jobInterval;

    /**
     * 间隔类型；0：毫秒；1：秒；2：分钟；3：小时
     */
    @TableField(value = "interval_type")
    private String intervalType;

    /**
     * 调度类型；0：简单调度；1：cron表达式调度
     */
    @TableField(value = "schedule_type")
    private String scheduleType;
}