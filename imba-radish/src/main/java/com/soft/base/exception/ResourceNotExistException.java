package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/3/28 15:56
 **/
public class ResourceNotExistException extends RuntimeException {

    public ResourceNotExistException(String message) {
        super(message);
    }
}
