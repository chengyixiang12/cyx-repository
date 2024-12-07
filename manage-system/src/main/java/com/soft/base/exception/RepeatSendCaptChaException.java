package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/7 19:04
 **/
public class RepeatSendCaptChaException extends Exception {

    private String message;

    public RepeatSendCaptChaException(String message) {
        super(message);
    }
}
