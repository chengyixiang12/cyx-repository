package com.soft.base.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogIgnore {
}
