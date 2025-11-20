package com.soft.base.websocket.handle.message.concrete;

import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.model.dto.UserDto;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.send.HeartBeatSendParams;
import com.soft.base.websocket.send.SendParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 心跳检测处理类，客户端每隔30秒调用一次
 * @DateTime: 2024/11/22 17:17
 **/
@Component
@Slf4j
public class HeartbeatHandler implements WebSocketConcreteHandler<String> {

    @Value(value = "${radish.token.expire-time}")
    private Long authorizationExpire;

    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public HeartbeatHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        try {
            UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
            String token = (String) session.getAttributes().get(WebSocketConstant.AUTHORIZATION);

            HeartBeatSendParams heartBeatSendParams = new HeartBeatSendParams();

            // 校验token是否过期，实现token无感刷新
            Long expire = redisTemplate.getExpire(RedisConstant.AUTHORIZATION_USERNAME + token);
            if (expire < 60) {
                token = UUID.randomUUID().toString();
                // 更新attributes的token
                session.getAttributes().put(WebSocketConstant.AUTHORIZATION, token);
                // 删掉之前的token
                redisTemplate.delete(RedisConstant.AUTHORIZATION_USERNAME + token);
                // 存储token
                redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token, userDto.getUsername(), authorizationExpire, TimeUnit.SECONDS);
                heartBeatSendParams.setToken(token);
                log.info("{} token refresh...", userDto.getUsername());
            }

            // 重新设置用户在线状态
            redisTemplate.opsForValue().set(RedisConstant.WS_USER_SESSION + userDto.getId(), userDto.getUsername(), RedisConstant.WS_USER_SESSION_EXPIRE, TimeUnit.SECONDS);
            WebSocketSessionManager.addSession(userDto.getId(), session);

            heartBeatSendParams.setStatus(true);
            heartBeatSendParams.setOrder(WebSocketOrderEnum.HEART_BEAT.toString());
            session.sendMessage(new TextMessage(heartBeatSendParams.toJsonString()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.HEART_BEAT;
    }
}
