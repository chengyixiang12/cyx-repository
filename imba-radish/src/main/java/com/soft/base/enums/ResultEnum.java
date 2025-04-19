package com.soft.base.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {

    SUCCESS(2001, ""),
    FAIL_NORMAL(5001, "服务异常，请联系管理员"),
    AUTHENTICATION_FAIL(5002, "认证过期，请重新登录"),
    PERMISSION_NOT_ENOUGH(5003, "权限不足"),
    BLACKLIST_TOKEN(5004, "认证失败，请重新登录"),
    NOT_AUTHENTICATION(5005, "未认证，请重新登录"),
    RATE_LIMIT(5006, "系统繁忙，请稍后再试"),
    ;

    private final Integer code;

    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
