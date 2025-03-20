package com.soft.base.websocket;

import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


/**
 * @Author: cyx
 * @Description: websocket拦截器
 * @DateTime: 2024/11/21 20:02
 **/
@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final UserDetailsService userDetailsService;

    private final RedisTemplate<String, Object> redisTemplate;

    public WebSocketInterceptor(UserDetailsService userDetailsService, RedisTemplate<String, Object> redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter(WebSocketConstant.AUTHORIZATION);
            String username = (String) redisTemplate.opsForValue().get(RedisConstant.AUTHORIZATION_USERNAME + token);
            if (StringUtils.isEmpty(username)) {
                log.error("token is expire...");
                return false;
            }
            UserDto user = (UserDto) userDetailsService.loadUserByUsername(username);
            attributes.put(WebSocketConstant.WEBSOCKET_USER, user);
            attributes.put(WebSocketConstant.AUTHORIZATION, token);
        } else {
            log.info("request is not ServletServerHttpRequest type");
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}
