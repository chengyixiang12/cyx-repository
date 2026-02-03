package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 调度任务表
 * @TableName sys_schedule_job
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_schedule_job")
@Data
public class SysScheduleJob extends BaseEntity {

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
    private Integer status;

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

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 作业执行类
     */
    @TableField(value = "job_class")
    private String jobClass;
}