package com.soft.base.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/27 15:30
 **/

@Data
@Schema(description = "获取日志响应参数")
@Alias(value = "GetLogVo")
public class GetLogVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String nickname;

    @Schema(description = "日志级别")
    private String logLevel;

    @Schema(description = "请求ip")
    private String ipAddress;

    @Schema(description = "访问路径")
    private String requestUrl;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseParams;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "耗时")
    private String executionTime;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "状态码")
    private String statusCode;

    @Schema(description = "异常信息")
    private String exceptionInfo;

    @Schema(description = "操作系统/浏览器")
    private String osBrowserInfo;
}
