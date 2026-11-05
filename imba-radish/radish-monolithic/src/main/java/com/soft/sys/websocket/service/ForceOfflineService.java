package com.soft.sys.websocket.service;

import com.soft.common.constants.RedisConstant;
import com.soft.common.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.send.ForceOfflineResponse;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: 强制下线服务（仅服务端内部调用）
 *
 * <p>安全约定：强制下线属于管理类敏感操作，触发入口必须经过服务端鉴权
 * （如 HTTP 管理接口的权限校验、登录互踢、改密码踢下线等内部逻辑）。
 * 不允许客户端通过 WebSocket 直接下发 FORCE_OFFLINE 指令，
 * 该指令未注册客户端处理器，分发器会直接拒绝。</p>
 *
 * <p>deviceType 为空表示该用户所有设备下线；指定时只下线对应设备类型。</p>
 *
 * @DateTime: 2026/11/05
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ForceOfflineService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 强制指定用户下线
     *
     * @param receiver   目标用户 id
     * @param deviceType 设备类型，为空表示所有设备
     * @param msg        下线提示消息
     */
    public void kick(Long receiver, String deviceType, String msg) throws IOException {
        String devicesKey = RedisConstant.USER_DEVICES + receiver;

        if (StringUtils.isBlank(deviceType)) {
            // 未指定设备类型：该用户所有设备下线（改密码、禁用等场景）
            Map<Object, Object> devices = redisTemplate.opsForHash().entries(devicesKey);
            if (devices.isEmpty()) {
                // 兼容旧数据：没有设备记录时按内存会话兜底
                for (WebSocketSession target : WebSocketSessionManager.getSessions(receiver)) {
                    kickDevice(target, receiver, null, null, msg);
                }
                return;
            }
            for (Map.Entry<Object, Object> entry : devices.entrySet()) {
                String type = String.valueOf(entry.getKey());
                String token = String.valueOf(entry.getValue());
                kickDevice(WebSocketSessionManager.getSession(receiver, type), receiver, type, token, msg);
            }
            return;
        }

        WebSocketSession target = WebSocketSessionManager.getSession(receiver, deviceType);
        String token = (String) redisTemplate.opsForHash().get(devicesKey, deviceType);
        if (target == null && StringUtils.isBlank(token)) {
            log.info("接收方未连接websocket...");
            return;
        }
        kickDevice(target, receiver, deviceType, token, msg);
    }

    /**
     * 发送强制下线消息，并清理该设备对应的 token、指纹和会话
     */
    private void kickDevice(WebSocketSession session, Long receiver, String deviceType, String token,
                            String msg) throws IOException {
        if (session != null && session.isOpen()) {
            ForceOfflineResponse response = new ForceOfflineResponse();
            response.setOrder(WebSocketOrderEnum.FORCE_OFFLINE.toString());
            response.setMsg(msg);
            session.sendMessage(new TextMessage(response.toJsonString()));
        }

        if (session != null) {
            Object attrToken = session.getAttributes().get(WebSocketConstant.AUTHORIZATION);
            if (StringUtils.isBlank(token) && attrToken != null) {
                token = String.valueOf(attrToken);
            }
            if (StringUtils.isBlank(deviceType)) {
                Object attrDeviceType = session.getAttributes().get(WebSocketConstant.DEVICE_TYPE);
                if (attrDeviceType != null) {
                    deviceType = String.valueOf(attrDeviceType);
                }
            }
        }

        if (StringUtils.isNotBlank(token)) {
            redisTemplate.delete(RedisConstant.AUTHORIZATION_USERNAME + token);
        }
        if (StringUtils.isNotBlank(deviceType)) {
            redisTemplate.opsForHash().delete(RedisConstant.USER_DEVICES + receiver, deviceType);
            redisTemplate.delete(RedisConstant.FINGERPRINT + receiver + ":" + deviceType);
            WebSocketSessionManager.removeSession(receiver, deviceType);
        }
    }
}
