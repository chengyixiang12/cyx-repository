package com.soft.base.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author cyq
 * @date 2026/3/28
 * @description 接口访问控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessControl {

    /**
     * 调用次数
     * @return
     */
    int times() default 1;

    /**
     * 间隔
     * @return
     */
    int interval() default 1;

    /**
     * 时间单位
     * @return
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 唯一标识
     * @return
     */
    String key() default "";
}
