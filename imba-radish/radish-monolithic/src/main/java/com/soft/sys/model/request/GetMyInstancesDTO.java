package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 获取我发起的流程列表请求参数
 * @DateTime: 2026/11/19 11:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取我发起的流程列表请求参数")
public class GetMyInstancesDTO extends PageDTO {

    @Schema(description = "关键字检索（流程名称/标题）")
    private String keyword;

    @Schema(description = "实例状态；1：运行中；2：已完成；3：已终止")
    private Integer status;
}
