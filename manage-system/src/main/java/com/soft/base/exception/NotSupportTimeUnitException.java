package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: 不支持的时间单位异常
 * @DateTime: 2024/12/7 9:33
 **/
public class NotSupportTimeUnitException extends Exception {

    public NotSupportTimeUnitException(String message) {
        super(message);
    }
}
