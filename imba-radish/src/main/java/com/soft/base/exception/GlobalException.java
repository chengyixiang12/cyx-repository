package com.soft.base.exception;

/**
 * 通用异常
 */
public class GlobalException extends RuntimeException {

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable e) {
        super(message, e);
    }
}
