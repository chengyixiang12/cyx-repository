package com.soft.base.core.aspect;

import com.soft.base.constants.RedisConstant;
import com.soft.base.core.annotation.AccessControl;
import com.soft.base.exception.GlobalException;
import com.soft.base.utils.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author cyq
 * @date 2026/3/28
 * @description 接口访问控制界面
 */
@Aspect
@Slf4j
@Component
@Order(20)
public class AccessControlAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    public AccessControlAspect(RedisTemplate<String, Object> redisTemplate, HttpServletRequest request) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(accessControl)")
    public Object around(ProceedingJoinPoint joinPoint, AccessControl accessControl) throws Throwable {
        int interval = accessControl.interval();
        int times = accessControl.times();
        TimeUnit unit = accessControl.unit();
        String key = accessControl.key();

        if (StringUtils.isBlank(key)) {
            HttpServletRequest servletRequest = ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
            key = CommonUtil.getIp(servletRequest);
        }

        Integer curTimes = (Integer) redisTemplate.opsForValue().get(RedisConstant.ACCESS_CONTROL + key);
        if (curTimes == null || curTimes < times) {
            redisTemplate.opsForValue().setIfAbsent(RedisConstant.ACCESS_CONTROL + key, times, interval, unit);
            redisTemplate.opsForValue().increment(RedisConstant.ACCESS_CONTROL + key);
            return joinPoint.proceed();
        } else {
            throw new GlobalException("已达访问上限，请稍候再试");
        }
    }
}
