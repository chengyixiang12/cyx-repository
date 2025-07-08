package com.soft.base.core.annotation;

import java.lang.annotation.*;

/**
 * @Author: cyx
 * @Description: 全局锁注解
 * @DateTime: 2025/4/12 16:23
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLock {

    /**
     * 唯一键
     * @return
     */
    String name();
}
