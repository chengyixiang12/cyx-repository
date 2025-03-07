package com.soft.base.enums;

/**
 * @Author: cyx
 * @Description: 日志类型枚举
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
     * 操作日志
     */
    OPERATION("3"),
    ;

    private final String code;

    LogTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
