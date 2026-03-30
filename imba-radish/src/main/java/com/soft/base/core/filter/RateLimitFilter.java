package com.soft.base.core.filter;

import com.soft.base.constants.HttpConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.enums.ResultEnum;
import com.soft.base.properties.RateLimitProperty;
import com.soft.base.resultapi.R;
import com.soft.base.utils.CommonUtil;
import com.soft.base.utils.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 限流拦截器，滑动窗口实现
 * @DateTime: 2024/11/28 17:21
 **/

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RateLimitProperty rateLimitProperty;

    public RateLimitFilter(RedisTemplate<String, Object> redisTemplate,
                           RateLimitProperty rateLimitProperty) {
        this.redisTemplate = redisTemplate;
        this.rateLimitProperty = rateLimitProperty;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (!rateLimitProperty.getEnable()) {
            return;
        }

        String key = CommonUtil.getIp(request);
        // 获取当前时间戳
        long currentTimestamp = System.currentTimeMillis();

        // 使用 Redis 存储请求的时间戳
        Long requestCount = redisTemplate.opsForZSet().count(RedisConstant.RATE_LIMIT_KEY + key, currentTimestamp - rateLimitProperty.getWindowSize() * 1000L, currentTimestamp);

        // 如果超过最大请求次数，拒绝请求
        if (requestCount != null && requestCount >= rateLimitProperty.getMaxRequest()) {
            ResponseUtil.writeMsg(response, HttpConstant.SUCCESS, R.fail(ResultEnum.RATE_LIMIT.getCode(), ResultEnum.RATE_LIMIT.getMessage()));
            return;
        }

        log.info("ip: {}, uri: {}", key, request.getRequestURI());

        // 记录请求时间戳
        redisTemplate.opsForZSet().add(RedisConstant.RATE_LIMIT_KEY + key, String.valueOf(currentTimestamp), currentTimestamp);

        // 设置请求过期时间为窗口大小，确保缓存不会无限增大
        redisTemplate.expire(RedisConstant.RATE_LIMIT_KEY + key, rateLimitProperty.getWindowSize(), TimeUnit.SECONDS);

        filterChain.doFilter(request, response);
    }
}
