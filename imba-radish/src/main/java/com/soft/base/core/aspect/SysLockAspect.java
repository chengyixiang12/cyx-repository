package com.soft.base.core.aspect;

import cn.hutool.core.util.IdUtil;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.core.annotation.SysLock;
import com.soft.base.enums.ResultEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.properties.RadishProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 全局锁
 * @DateTime: 2025/4/12 16:32
 **/

@Aspect
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class SysLockAspect {

    private final RadishProperty radishProperty;

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(sysLock)")
    public Object around(ProceedingJoinPoint joinPoint, SysLock sysLock) throws Throwable {
        String key = sysLock.name();
        int retryCount = BaseConstant.INTEGER_INIT_VAL;
        String lockFlag = IdUtil.fastSimpleUUID();
        boolean locked = false;
        try {
            while (retryCount < radishProperty.getLock().getMaxRetry()) {
                Boolean success = redisTemplate.opsForValue().setIfAbsent(RedisConstant.LOCK_KEY + key, lockFlag, radishProperty.getLock().getExpire(), TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(success)) {
                    locked = true;
                    break;
                }
                retryCount++;
                TimeUnit.MILLISECONDS.sleep(radishProperty.getLock().getRetryIntervalTime());
            }

            if (!locked) {
                throw new GlobalException(ResultEnum.RATE_LIMIT.getMessage());
            }

            return joinPoint.proceed();

        } finally {
            // 释放锁时确保是当前线程加的锁
            if (locked && lockFlag.equals(redisTemplate.opsForValue().get(RedisConstant.LOCK_KEY + key))) {
                redisTemplate.delete(RedisConstant.LOCK_KEY + key);
            }
        }
    }

}
