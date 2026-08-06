package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.ForceOfflineRequest;
import com.soft.sys.websocket.send.ForceOfflineResponse;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: 强制下线处理器
 * @DateTime: 2024/11/22 0:10
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class ForceOfflineHandler implements WebSocketConcreteHandler<String> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        ForceOfflineRequest forceOfflineRecParam = JSON.parseObject(message.getPayload(), ForceOfflineRequest.class);
        WebSocketSession receiveSession = WebSocketSessionManager.getSession(forceOfflineRecParam.getReceiver());
        if (receiveSession == null) {
            log.info("接收方未连接websocket...");
            return;
        }

        ForceOfflineResponse forceOfflineSendParams = new ForceOfflineResponse();
        forceOfflineSendParams.setOrder(forceOfflineRecParam.getOrder());
        forceOfflineSendParams.setMsg(forceOfflineRecParam.getMsg());
        receiveSession.sendMessage(new TextMessage(forceOfflineSendParams.toJsonString()));

        UserDTO userDto = (UserDTO) receiveSession.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();

        String token = (String) receiveSession.getAttributes().get(WebSocketConstant.AUTHORIZATION);
        WebSocketSessionManager.removeSession(userDto.getId());
        Set<String> keySet = new HashSet<>(4);
        keySet.add(RedisConstant.AUTHORIZATION_USERNAME + token);
        keySet.add(RedisConstant.USER_INFO + username);
        keySet.add(RedisConstant.FINGERPRINT + username);
        redisTemplate.delete(keySet);
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FORCE_OFFLINE;
    }
}
