package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: 数值范围异常
 * @DateTime: 2024/12/7 9:15
 **/
public class NumberRangeException extends RuntimeException {

    public NumberRangeException(String message) {
        super(message);
    }
}
