package com.soft.base.async;

import com.soft.base.constants.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: 清除密钥缓存
 * @DateTime: 2025/1/4 10:31
 **/

@Component
public class ClearSecretKeyCacheAsync {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ClearSecretKeyCacheAsync(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 异步清除缓存密钥
     * @param type
     */
    @Async
    public void clear(Integer type) {
        Set<String> keys = new HashSet<>();
        keys.add(RedisConstant.RSA_PUBLIC_KEY + type);
        keys.add(RedisConstant.RSA_PRIVATE_KEY + type);
        redisTemplate.delete(keys);
    }
}
