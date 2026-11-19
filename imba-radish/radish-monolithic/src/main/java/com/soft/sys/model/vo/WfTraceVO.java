package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: cyx
 * @Description: 流程跟踪详情
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "流程跟踪详情")
@Alias(value = "WfTraceVO")
public class WfTraceVO {

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

    @Schema(description = "执行引擎标识")
    private String engine;

    @Schema(description = "发起人昵称")
    private String startUserNickname;

    @Schema(description = "发起时间")
    private LocalDateTime createTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "任务列表（含待办与已办）")
    private List<WfTasksVO> tasks;

    @Schema(description = "审批留痕列表")
    private List<WfRecordsVO> records;
}
