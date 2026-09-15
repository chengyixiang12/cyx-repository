package com.soft.sys.async;

import com.soft.sys.constants.RedisConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import com.soft.sys.websocket.handler.PingHandler;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;

/**
 *
 * @author cyx
 * @date 2026-06-18
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SendPingAsync {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    public void send(String sessionKey) {
        int TIME_OUT = 40;
        Boolean pingStatus = redisTemplate.opsForValue().setIfAbsent(RedisConstant.PING_STATUS + sessionKey, "1", Duration.ofSeconds(TIME_OUT));
        if (Boolean.FALSE.equals(pingStatus)) {
            WebSocketSessionManager.removeSession(Long.parseLong(sessionKey));
        } else {
            WebSocketSession session = WebSocketSessionManager.getSession(Long.parseLong(sessionKey));
            PingHandler pingHandler = (PingHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.PING.toString());
            try {
                pingHandler.handle(session, new TextMessage("ping"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                redisTemplate.delete(RedisConstant.PING_STATUS + sessionKey);
            }
        }
    }
}
