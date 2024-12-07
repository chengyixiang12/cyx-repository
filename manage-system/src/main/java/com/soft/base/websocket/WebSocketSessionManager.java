package com.soft.base.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: cyx
 * @Description: websocket连接会话管理
 * @DateTime: 2024/11/21 23:26
 **/
public class WebSocketSessionManager {

    private static final Map<String, WebSocketSession> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 添加用户session
     * @param sessionKey
     * @param session
     */
    public static void addSession(Long sessionKey, WebSocketSession session) {
        USER_SESSION_MAP.put(String.valueOf(sessionKey), session);
    }

    /**
     * 删除用户session
     * @param sessionKey
     */
    public static void removeSession(Long sessionKey) throws RuntimeException {
        try {
            getSession(sessionKey).close();
            USER_SESSION_MAP.remove(String.valueOf(sessionKey));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取用户session
     * @param sessionKey
     * @return
     */
    public static WebSocketSession getSession(Long sessionKey) {
        return USER_SESSION_MAP.get(String.valueOf(sessionKey));
    }

    /**
     * 获取所有的key
     * @return
     */
    public static Set<String> getKeys() {
        return new HashSet<>(USER_SESSION_MAP.keySet());
    }

    /**
     * 清空用户会话
     */
    public static void clear() {
        USER_SESSION_MAP.clear();
    }
}
