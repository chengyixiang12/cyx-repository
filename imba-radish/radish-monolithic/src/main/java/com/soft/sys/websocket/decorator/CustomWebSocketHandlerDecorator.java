package com.soft.sys.websocket.decorator;

import com.soft.common.constants.RedisConstant;
import com.soft.common.constants.WebSocketConstant;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * @Author: cyx
 * @Description: websocket装饰器，用于处理连接和断开操作
 * @DateTime: 2024/11/21 23:12
 **/
@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private final RedisTemplate<String, Object> redisTemplate;

    public CustomWebSocketHandlerDecorator(WebSocketHandler delegate, RedisTemplate<String, Object> redisTemplate) {
        super(delegate);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String deviceType = (String) session.getAttributes().get(WebSocketConstant.DEVICE_TYPE);
        String sessionKey = WebSocketSessionManager.sessionKey(userDto.getId(), deviceType);
        // 删除上一次连接的ping状态
        redisTemplate.delete(RedisConstant.PING_STATUS + sessionKey);
        // 同一设备类型只保留一个连接，关闭旧连接后登记新连接
        WebSocketSessionManager.removeSession(userDto.getId(), deviceType);
        WebSocketSessionManager.addSession(userDto.getId(), deviceType, session);
        log.info("{} connected, deviceType={}", userDto.getUsername(), deviceType);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus closeStatus) {
        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String deviceType = (String) session.getAttributes().get(WebSocketConstant.DEVICE_TYPE);
        String sessionKey = WebSocketSessionManager.sessionKey(userDto.getId(), deviceType);
        // 只有当前连接仍是缓存中的连接时才移除，避免旧连接关闭误删同类型新连接
        if (WebSocketSessionManager.getSession(userDto.getId(), deviceType) == session) {
            WebSocketSessionManager.removeSession(userDto.getId(), deviceType);
            redisTemplate.delete(RedisConstant.PING_STATUS + sessionKey);
        }
        log.info("{} connect closed, deviceType={}", userDto.getUsername(), deviceType);
    }


}
