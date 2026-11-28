package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 获取待办/已办任务列表请求参数
 * @DateTime: 2026/11/19 11:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取待办/已办任务列表请求参数")
public class GetWorkflowTasksDTO extends PageDTO {

    @Schema(description = "关键字检索（流程名称/标题）")
    private String keyword;
}
