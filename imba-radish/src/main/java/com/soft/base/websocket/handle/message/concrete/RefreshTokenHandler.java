package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.model.dto.UserDto;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.RefreshTokenRecParm;
import com.soft.base.websocket.send.RefreshTokenSendParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${radish.token.expire-time}")
    private Long authorizationExpire;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {

        RefreshTokenRecParm refreshTokenRecParm = JSON.parseObject(message.getPayload(), RefreshTokenRecParm.class);
        String fingerprint = refreshTokenRecParm.getFingerprint();

        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);

        RefreshTokenSendParam refreshTokenSendParam = new RefreshTokenSendParam();
        refreshTokenSendParam.setOrder(WebSocketOrderEnum.REFRESH_TOKEN.toString());

        String fingerprintCache = (String) redisTemplate.opsForValue().get(RedisConstant.FINGERPRINT + userDto.getUsername());
        if (StringUtils.isBlank(fingerprintCache) || !fingerprintCache.equals(fingerprint)) {
            refreshTokenSendParam.setMsg("token刷新失败");
            log.info("token refresh fail");
            session.sendMessage(new TextMessage(refreshTokenSendParam.toJsonString()));
            return;
        }

        String token = UUID.randomUUID().toString();
        // 更新attributes的token
        session.getAttributes().put(WebSocketConstant.AUTHORIZATION, token);
        // 删掉之前的token
        redisTemplate.delete(RedisConstant.AUTHORIZATION_USERNAME + token);
        // 存储token
        redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token, userDto.getUsername(), authorizationExpire, TimeUnit.SECONDS);
        log.info("{} token refresh...", userDto.getUsername());
        refreshTokenSendParam.setToken(token);
        session.sendMessage(new TextMessage(refreshTokenSendParam.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.REFRESH_TOKEN;
    }
}
