package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 定时任务执行记录
 * @TableName sys_schedule_record
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_schedule_record")
@Data
public class SysScheduleRecord extends BaseEntity {

    /**
     * sys_schedule_job主键
     */
    @TableField(value = "job_id")
    private Long jobId;

    /**
     * 日志明细
     */
    @TableField(value = "log_info")
    private String logInfo;

    /**
     * 任务名称
     */
    @TableField(value = "job_name")
    private String jobName;

    /**
     * 任务类型
     */
    @TableField(value = "job_type")
    private String jobType;

    /**
     * 任务分组
     */
    @TableField(value = "job_group")
    private String jobGroup;

    /**
     * 耗时
     */
    @TableField(value = "execution_time")
    private Long executionTime;

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
     * 任务参数
     */
    @TableField(value = "job_param")
    private String jobParam;

    /**
     * 执行状态；0：未执行；1：执行中；2：执行完成；3：执行失败
     */
    @TableField(value = "status")
    private String status;
}