package com.soft.base.intercept;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.enums.ResultEnum;
import com.soft.base.properties.RateLimitProperty;
import com.soft.base.resultapi.R;
import com.soft.base.utils.ResponseUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 限流拦截器，滑动窗口实现
 * @DateTime: 2024/11/28 17:21
 **/

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RateLimitProperty rateLimitProperty;

    public RateLimitInterceptor(RedisTemplate<String, Object> redisTemplate,
                                RateLimitProperty rateLimitProperty) {
        this.redisTemplate  = redisTemplate;
        this.rateLimitProperty = rateLimitProperty;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
//        String clientIp = request.getRemoteAddr().replaceAll(BaseConstant.ESCAPE_CHARACTER + BaseConstant.ENG_COLON, BaseConstant.BLANK_CHARACTER); // 获取客户端 IP 地址
        String requestURI = request.getRequestURI().replaceAll(BaseConstant.LEFT_SLASH, BaseConstant.BLANK_CHARACTER);

        if (rateLimitProperty.getEnable()) {
            String key = RedisConstant.RATE_LIMIT_KEY + requestURI;
            // 获取当前时间戳
            long currentTimestamp = System.currentTimeMillis();

            // 使用 Redis 存储请求的时间戳
            Long requestCount = redisTemplate.opsForZSet().count(key, currentTimestamp - rateLimitProperty.getWindowSize() * BaseConstant.WINDOW_SIZE_EXPAND_MULTIPLE, currentTimestamp);

            // 如果超过最大请求次数，拒绝请求
            if (requestCount != null && requestCount >= rateLimitProperty.getMaxRequest()) {
                ResponseUtil.writeErrMsg(response, HttpResponseStatus.OK.code(), R.fail(ResultEnum.RATE_LIMIT.getCode(), ResultEnum.RATE_LIMIT.getMessage()));
                return false;
            }

            // 记录请求时间戳
            redisTemplate.opsForZSet().add(key, String.valueOf(currentTimestamp), currentTimestamp);

            // 设置请求过期时间为窗口大小，确保缓存不会无限增大
            redisTemplate.expire(key, rateLimitProperty.getWindowSize(), TimeUnit.SECONDS);
        }

        return true;
    }
}
