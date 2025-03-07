package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 11:27
 **/

@Data
@Schema(description = "获取日志响应参数")
public class LogsVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "耗时")
    private Long executionTime;
}
