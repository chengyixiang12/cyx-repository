package com.soft.base.exception;

/**
 * @Author: 程益祥
 * @Description: TODO
 * @DateTime: 2024/11/7 18:17
 **/
public class CaptChaErrorException extends RuntimeException {

    private String message;

    public CaptChaErrorException(String message) {
        super(message);
    }
}