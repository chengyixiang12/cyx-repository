package com.soft.base.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 10:59
 **/

@Data
@Schema(description = "日志传输参数")
public class LogDto implements Serializable {

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
    private String responseResult;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "日志来源")
    private String source;

    @Schema(description = "耗时")
    private Long executionTime;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "状态码")
    private Integer statusCode;

    @Schema(description = "异常信息")
    private String exceptionInfo;

    @Schema(description = "操作系统/浏览器信息")
    private String osBrowserInfo;

    @Schema(description = "创建者")
    private Long createBy;

    @Schema(description = "日志类型")
    private String type;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
