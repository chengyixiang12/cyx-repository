package com.soft.sys.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {

    SUCCESS(10000, "成功"),
    FAIL_NORMAL(10002, "服务异常，请联系管理员"),
    AUTHENTICATION_FAIL(10003, "认证过期，请重新登录"),
    PERMISSION_NOT_ENOUGH(10004, "权限不足"),
    BLACKLIST_TOKEN(10005, "认证失败，请重新登录"),
    NOT_AUTHENTICATION(10006, "未认证，请重新登录"),
    RATE_LIMIT(10007, "系统繁忙，请稍后再试"),
    ;

    private final Integer code;

    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
