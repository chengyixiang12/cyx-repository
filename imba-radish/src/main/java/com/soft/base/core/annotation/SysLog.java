package com.soft.base.core.annotation;

import com.soft.base.enums.LogModuleEnum;
import com.soft.base.enums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * @Author: cyx
 * @Description: 日志注解
 * @DateTime: 2024/11/21 9:21
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 日志描述
     * @return
     */
    String value() default "";

    /**
     * 日志模块名称
     * @return
     */
    LogModuleEnum module() default LogModuleEnum.DEFAULT;

    /**
     * 日志类型
     * @return
     */
    LogTypeEnum type() default LogTypeEnum.OPERATION;
}
