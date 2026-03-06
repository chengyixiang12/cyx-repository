package com.soft.base.websocket.handle.message.concrete;

import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.model.dto.UserDto;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.send.HeartBeatSendParams;
import com.soft.base.websocket.send.SendParams;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class HeartbeatHandler implements WebSocketConcreteHandler<String> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        try {
            UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

            // 重新设置用户在线状态
            redisTemplate.opsForValue().set(RedisConstant.WS_USER_SESSION + userDto.getId(), userDto.getUsername(), RedisConstant.WS_USER_SESSION_EXPIRE, TimeUnit.SECONDS);
            WebSocketSessionManager.addSession(userDto.getId(), session);

            String token = (String) session.getAttributes().get(WebSocketConstant.AUTHORIZATION);
            // 校验token是否过期，实现token无感刷新
            Long expire = redisTemplate.getExpire(RedisConstant.AUTHORIZATION_USERNAME + token);
            HeartBeatSendParams heartBeatSendParams = new HeartBeatSendParams();
            if (expire < 60) {
                heartBeatSendParams.setRefreshFlag(true);
            }
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
