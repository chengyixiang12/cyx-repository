package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/7 18:17
 **/
public class CaptChaErrorException extends Exception {

    private String message;

    public CaptChaErrorException(String message) {
        super(message);
    }
}
