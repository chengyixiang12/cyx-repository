package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 发起流程请求参数
 * @DateTime: 2026/11/19 11:30
 **/
@Data
@Schema(description = "发起流程请求参数")
@Alias(value = "StartWorkflowDTO")
public class StartWorkflowDTO {

    @Schema(description = "流程标识")
    @NotBlank(message = "流程标识不能为空")
    @Size(max = 64, message = "流程标识长度不能超过64")
    private String processKey;

    @Schema(description = "流程名称")
    @NotBlank(message = "流程名称不能为空")
    @Size(max = 100, message = "流程名称长度不能超过100")
    private String processName;

    @Schema(description = "业务单据标识")
    @Size(max = 128, message = "业务单据标识长度不能超过128")
    private String businessKey;

    @Schema(description = "流程标题")
    @Size(max = 200, message = "流程标题长度不能超过200")
    private String title;

    @Schema(description = "审批人id序列，按顺序依次审批")
    @NotEmpty(message = "审批人不能为空")
    private List<Long> assigneeIds;

    @Schema(description = "发起备注")
    @Size(max = 500, message = "发起备注长度不能超过500")
    private String comment;
}
