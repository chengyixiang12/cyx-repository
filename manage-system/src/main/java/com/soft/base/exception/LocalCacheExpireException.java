package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: 本地缓存过期异常
 * @DateTime: 2024/12/7 10:46
 **/
public class LocalCacheExpireException extends Exception {

    public LocalCacheExpireException(String message) {
        super(message);
    }
}
