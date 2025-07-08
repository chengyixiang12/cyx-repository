package com.soft.base.core.aspect;

import com.soft.base.core.annotation.SysLock;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.exception.GlobalException;
import com.soft.base.utils.UniversalUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 锁切面
 * @DateTime: 2025/4/12 16:32
 **/

@Aspect
@Slf4j
@Component
@Order(1)
public class SysLockAspect {

    @Value("${radish.lock.expire}")
    private Long lockExpire;

    @Value("${radish.lock.max-retry}")
    private Integer maxRetry;

    @Value("${radish.lock.retry-interval-time}")
    private Long retryIntervalTime;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UniversalUtil universalUtil;

    @Autowired
    public SysLockAspect(RedisTemplate<String, Object> redisTemplate, UniversalUtil universalUtil) {
        this.redisTemplate = redisTemplate;
        this.universalUtil = universalUtil;
    }

    @Around("@annotation(sysLock)")
    public Object around(ProceedingJoinPoint joinPoint, SysLock sysLock) throws Throwable {
        String key = sysLock.name();
        int retryCount = BaseConstant.INTEGER_INIT_VAL;
        String lockFlag = universalUtil.fileKeyGen();
        boolean locked = false;
        try {
            while (retryCount < maxRetry) {
                Boolean success = redisTemplate.opsForValue().setIfAbsent(RedisConstant.LOCK_KEY + key, lockFlag, lockExpire, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(success)) {
                    locked = true;
                    break;
                }
                retryCount++;
                TimeUnit.MILLISECONDS.sleep(retryIntervalTime);
            }

            if (!locked) {
                throw new GlobalException("系统繁忙，请稍后再试");
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
