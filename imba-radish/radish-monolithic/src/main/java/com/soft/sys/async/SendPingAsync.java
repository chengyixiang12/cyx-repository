package com.soft.sys.async;

import com.soft.common.constants.RedisConstant;
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
        Long userId = WebSocketSessionManager.parseUserId(sessionKey);
        String deviceType = WebSocketSessionManager.parseDeviceType(sessionKey);
        String pingKey = RedisConstant.PING_STATUS + sessionKey;
        Boolean pingStatus = redisTemplate.opsForValue().setIfAbsent(pingKey, "1", Duration.ofSeconds(TIME_OUT));
        if (Boolean.FALSE.equals(pingStatus)) {
            WebSocketSessionManager.removeSession(userId, deviceType);
        } else {
            WebSocketSession session = WebSocketSessionManager.getSession(userId, deviceType);
            if (session == null) {
                redisTemplate.delete(pingKey);
                return;
            }
            PingHandler pingHandler = (PingHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.PING.toString());
            try {
                pingHandler.handle(session, new TextMessage("ping"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                redisTemplate.delete(pingKey);
            }
        }
    }
}
