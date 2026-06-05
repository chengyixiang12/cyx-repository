package com.soft.sys.core.conf;

import com.soft.sys.websocket.WebSocketInterceptor;
import com.soft.sys.websocket.handle.CustomWebSocketHandlerDecorator;
import com.soft.sys.websocket.handle.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置：注册处理器、拦截器与跨域策略
 *
 * @author cyx
 * @date 2024-11-21
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                        new CustomWebSocketHandlerDecorator(new WebSocketHandler(), redisTemplate),
                        "/ws")
                .addInterceptors(new WebSocketInterceptor(userDetailsService, redisTemplate))
                .setAllowedOrigins("*");
    }
}
