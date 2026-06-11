package com.soft.sys.websocket.interceptor;

import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


/**
 * @Author: cyx
 * @Description: websocket拦截器
 * @DateTime: 2024/11/21
 **/
@Slf4j
public record WebSocketInterceptor(UserDetailsService userDetailsService,
                                   RedisTemplate<String, Object> redisTemplate) implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter(WebSocketConstant.AUTHORIZATION);
            String username = (String) redisTemplate.opsForValue().get(RedisConstant.AUTHORIZATION_USERNAME + token);
            if (StringUtils.isEmpty(username)) {
                log.warn("token is expired...");
                return false;
            }
            UserDto user = (UserDto) userDetailsService.loadUserByUsername(username);
            attributes.put(WebSocketConstant.WEBSOCKET_USER, user);
            attributes.put(WebSocketConstant.AUTHORIZATION, token);
        } else {
            log.warn("request is not ServletServerHttpRequest type");
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception exception) {

    }
}
