package com.soft.base.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String nickname;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "耗时")
    private Long executionTime;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "日志级别")
    private String logLevel;
}
