package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/7 11:48
 **/
public class InvalidLoginMethodException extends RuntimeException {

    public InvalidLoginMethodException(String message) {
        super(message);
    }
}
