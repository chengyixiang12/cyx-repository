package com.soft.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 *
 * @TableName wf_task
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value ="wf_task")
@Alias(value = "WfTask")
public class WfTask extends BaseEntity {

    /**
     * 流程实例id
     */
    @TableField(value = "instance_id")
    private Long instanceId;

    /**
     * 节点序号；从1开始
     */
    @TableField(value = "node_order")
    private Integer nodeOrder;

    /**
     * 任务节点标识
     */
    @TableField(value = "task_key")
    private String taskKey;

    /**
     * 任务节点名称
     */
    @TableField(value = "task_name")
    private String taskName;

    /**
     * 办理人id
     */
    @TableField(value = "assignee_id")
    private Long assigneeId;

    /**
     * 任务状态；1：待办；2：已办；3：已取消
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 办理动作；1：通过；2：驳回；3：转办
     */
    @TableField(value = "action")
    private Integer action;

    /**
     * 审批意见
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 任务到达时间
     */
    @TableField(value = "arrive_time")
    private LocalDateTime arriveTime;

    /**
     * 办理时间
     */
    @TableField(value = "handle_time")
    private LocalDateTime handleTime;
}
