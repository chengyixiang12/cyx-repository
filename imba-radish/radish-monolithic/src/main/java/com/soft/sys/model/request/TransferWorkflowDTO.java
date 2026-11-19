package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: 转办流程任务请求参数
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "转办流程任务请求参数")
@Alias(value = "TransferWorkflowDTO")
public class TransferWorkflowDTO {

    @Schema(description = "任务id")
    @NotNull(message = "任务id不能为空")
    private Long taskId;

    @Schema(description = "转办目标用户id")
    @NotNull(message = "转办目标用户不能为空")
    private Long targetUserId;

    @Schema(description = "转办意见")
    @Size(max = 500, message = "转办意见长度不能超过500")
    private String comment;
}
