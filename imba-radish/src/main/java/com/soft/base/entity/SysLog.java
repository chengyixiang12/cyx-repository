package com.soft.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志表
 * @TableName sys_log
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="sys_log")
@Data
public class SysLog extends BaseEntity {

    /**
     * 日志级别
     */
    @TableField(value = "log_level")
    private String logLevel;

    /**
     * 请求ip
     */
    @TableField(value = "ip_address")
    private String ipAddress;

    /**
     * 访问路径
     */
    @TableField(value = "request_url")
    private String requestUrl;

    /**
     * 请求方法
     */
    @TableField(value = "request_method")
    private String requestMethod;

    /**
     * 请求参数
     */
    @TableField(value = "request_params")
    private String requestParams;

    /**
     * 响应结果
     */
    @TableField(value = "response_result")
    private String responseResult;

    /**
     * 操作描述
     */
    @TableField(value = "operation_desc")
    private String operationDesc;

    /**
     * 日志来源
     */
    @TableField(value = "source")
    private String source;

    /**
     * 耗时
     */
    @TableField(value = "execution_time")
    private Long executionTime;

    /**
     * 模块名称
     */
    @TableField(value = "module_name")
    private String moduleName;

    /**
     * 状态码
     */
    @TableField(value = "status_code")
    private Integer statusCode;

    /**
     * 异常信息
     */
    @TableField(value = "exception_info")
    private String exceptionInfo;

    /**
     * 操作系统/浏览器信息
     */
    @TableField(value = "os_browser_info")
    private String osBrowserInfo;

    /**
     * 日志类型
     */
    @TableField(value = "type")
    private Integer type;
}