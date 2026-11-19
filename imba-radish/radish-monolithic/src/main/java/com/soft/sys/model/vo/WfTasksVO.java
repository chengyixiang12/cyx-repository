package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 待办/已办任务行
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "待办/已办任务行")
@Alias(value = "WfTasksVO")
public class WfTasksVO {

    @Schema(description = "任务id")
    private String id;

    @Schema(description = "流程实例id")
    private String instanceId;

    @Schema(description = "流程标识")
    private String processKey;

    @Schema(description = "流程名称")
    private String processName;

    @Schema(description = "流程标题")
    private String title;

    @Schema(description = "业务单据标识")
    private String businessKey;

    @Schema(description = "当前节点名称")
    private String currentNode;

    @Schema(description = "任务节点名称")
    private String taskName;

    @Schema(description = "任务到达时间")
    private LocalDateTime arriveTime;

    @Schema(description = "办理时间")
    private LocalDateTime handleTime;

    @Schema(description = "办理动作；1：通过；2：驳回；3：转办")
    private Integer action;

    @Schema(description = "审批意见")
    private String comment;

    @Schema(description = "发起人昵称")
    private String startUserNickname;

    @Schema(description = "办理人昵称")
    private String assigneeNickname;
}
