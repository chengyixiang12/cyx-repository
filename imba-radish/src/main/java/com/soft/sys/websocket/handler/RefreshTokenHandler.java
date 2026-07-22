package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.RefreshTokenRequest;
import com.soft.sys.websocket.send.RefreshTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author cyq
 * @date 2025/11/21
 * @description
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenHandler implements WebSocketConcreteHandler<String> {

    private final RadishProperty radishProperty;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {

        RefreshTokenRequest refreshTokenRecParm = JSON.parseObject(message.getPayload(), RefreshTokenRequest.class);
        String fingerprint = refreshTokenRecParm.getFingerprint();

        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

        RefreshTokenResponse refreshTokenSendParam = new RefreshTokenResponse();
        refreshTokenSendParam.setOrder(WebSocketOrderEnum.REFRESH_TOKEN.toString());

        String fingerprintCache = (String) redisTemplate.opsForValue().get(RedisConstant.FINGERPRINT + userDto.getUsername());
        if (StringUtils.isBlank(fingerprintCache) || !fingerprintCache.equals(fingerprint)) {
            refreshTokenSendParam.setMsg("token刷新失败");
            log.warn("token refresh fail");
            session.sendMessage(new TextMessage(refreshTokenSendParam.toJsonString()));
            return;
        }

        String token = UUID.randomUUID().toString();
        // 更新attributes的token
        session.getAttributes().put(WebSocketConstant.AUTHORIZATION, token);
        // 存储token
        redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token, userDto.getUsername(), radishProperty.getToken().getExpireTime(), TimeUnit.SECONDS);
        log.info("{} token refreshed...", userDto.getUsername());
        refreshTokenSendParam.setToken(token);
        session.sendMessage(new TextMessage(refreshTokenSendParam.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.REFRESH_TOKEN;
    }
}
