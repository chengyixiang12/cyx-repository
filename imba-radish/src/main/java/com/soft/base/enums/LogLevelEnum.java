package com.soft.base.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 日志级别
 * @DateTime: 2024/11/24 22:37
 **/

@Getter
public enum LogLevelEnum {

    /**
     * 普通
     */
    INFO("1", "info"),

    /**
     * 警告
     */
    WARN("2", "warn"),

    /**
     * 错误
     */
    ERROR("3", "error");

    private final String code;

    private final String name;

    LogLevelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
