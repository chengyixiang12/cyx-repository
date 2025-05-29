package com.soft.base.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 11:28
 **/

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取日志请求参数")
public class LogsRequest extends PageRequest {

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "日志级别")
    private String logLevel;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
