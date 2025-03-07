package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/25 23:52
 **/
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
