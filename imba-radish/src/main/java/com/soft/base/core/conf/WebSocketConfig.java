package com.soft.base.core.conf;

import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.WebSocketInterceptor;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.CustomWebSocketHandlerDecorator;
import com.soft.base.websocket.handle.WebSocketHandler;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: websocket配置类
 * @DateTime: 2024/11/21 19:44
 **/
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserDetailsService userDetailsService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public WebSocketConfig(UserDetailsService userDetailsService, RedisTemplate<String, Object> redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomWebSocketHandlerDecorator(new WebSocketHandler(), redisTemplate), "/ws")
                .addInterceptors(new WebSocketInterceptor(userDetailsService, redisTemplate))
                .setAllowedOrigins("*");
    }
}
