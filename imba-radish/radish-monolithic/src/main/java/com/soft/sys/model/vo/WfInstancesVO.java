package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 我发起的流程行
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "我发起的流程行")
@Alias(value = "WfInstancesVO")
public class WfInstancesVO {

    @Schema(description = "流程实例id")
    private String id;

    @Schema(description = "流程标识")
    private String processKey;

    @Schema(description = "流程名称")
    private String processName;

    @Schema(description = "流程标题")
    private String title;

    @Schema(description = "业务单据标识")
    private String businessKey;

    @Schema(description = "实例状态；1：运行中；2：已完成；3：已终止")
    private Integer status;

    @Schema(description = "当前节点名称")
    private String currentNode;

    @Schema(description = "当前待办人昵称（多个用逗号分隔）")
    private String currentAssigneeNickname;

    @Schema(description = "发起时间")
    private LocalDateTime createTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
