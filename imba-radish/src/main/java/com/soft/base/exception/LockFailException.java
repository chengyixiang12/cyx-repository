package com.soft.base.exception;

/**
 * @Author: cyx
 * @Description: 锁失败
 * @DateTime: 2025/4/12 17:30
 **/
public class LockFailException extends RuntimeException {

    public LockFailException(String msg) {
        super(msg);
    }
}
