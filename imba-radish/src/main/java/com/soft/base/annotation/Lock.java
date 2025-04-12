package com.soft.base.annotation;

import java.lang.annotation.*;

/**
 * @Author: cyx
 * @Description: 锁注解
 * @DateTime: 2025/4/12 16:23
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁值
     * @return
     */
    String value() default "";
}
