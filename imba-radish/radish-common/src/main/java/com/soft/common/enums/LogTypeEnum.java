package com.soft.common.enums;

/**
 * @Author: cyx
 * @Description: 日志类型枚举，code 对应 sys_log.type 字段
 * @DateTime: 2024/11/24 20:42
 **/
public enum LogTypeEnum {

    /**
     * 登录日志
     */
    LOGIN("1"),

    /**
     * 注册日志
     */
    REGISTER("2"),

    /**
     * 业务操作日志
     */
    OPERATION("3"),
    ;

    /**
     * 落库值，对应 sys_log.type
     */
    private final String code;

    LogTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
