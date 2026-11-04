package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/21 11:28
 **/

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取日志请求参数")
public class LogsDTO extends PageDTO {

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "日志级别")
    private String logLevel;

    @Schema(description = "日志类型：1-登录日志，2-注册日志，3-业务操作日志")
    private String type;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
