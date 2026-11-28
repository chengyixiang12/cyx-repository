package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: 办理流程任务请求参数（通过/驳回）
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "办理流程任务请求参数")
@Alias(value = "HandleWorkflowDTO")
public class HandleWorkflowDTO {

    @Schema(description = "任务id")
    @NotNull(message = "任务id不能为空")
    private Long taskId;

    @Schema(description = "审批意见")
    @Size(max = 500, message = "审批意见长度不能超过500")
    private String comment;
}
