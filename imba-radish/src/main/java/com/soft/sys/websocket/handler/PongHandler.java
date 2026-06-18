package com.soft.sys.websocket.handler;

import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDto;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.send.HeartBeatSendParams;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 心跳接收处理类
 * @DateTime: 2024/11/22 17:17
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class PongHandler implements WebSocketConcreteHandler<String> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        try {
            UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

            WebSocketSessionManager.addSession(userDto.getId(), session);

            String token = (String) session.getAttributes().get(WebSocketConstant.AUTHORIZATION);

            // 校验token是否过期，实现token无感刷新
            Long expire = redisTemplate.getExpire(RedisConstant.AUTHORIZATION_USERNAME + token);
            HeartBeatSendParams heartBeatSendParams = new HeartBeatSendParams();
            if (expire < 60) {
                heartBeatSendParams.setRefreshFlag(true);
            }
            heartBeatSendParams.setOrder(WebSocketOrderEnum.PONG.toString());
            session.sendMessage(new TextMessage(heartBeatSendParams.toJsonString()));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.PONG;
    }
}
