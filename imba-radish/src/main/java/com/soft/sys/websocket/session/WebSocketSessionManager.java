package com.soft.sys.websocket.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: cyx
 * @Description: websocket连接会话管理
 * @DateTime: 2024/11/21 23:26
 **/

@Slf4j
public class WebSocketSessionManager {

    private static final Map<String, WebSocketSession> USER_SESSION_CACHE = new ConcurrentHashMap<>();

    /**
     * 添加用户session
     * @param sessionKey
     * @param session
     */
    public static void addSession(Long sessionKey, WebSocketSession session) {
        USER_SESSION_CACHE.put(String.valueOf(sessionKey), session);
    }

    /**
     * 删除用户session
     * @param sessionKey
     */
    @SuppressWarnings("resource")
    public static void removeSession(Long sessionKey) throws RuntimeException {
        try {
            WebSocketSession session = getSession(sessionKey);
            if (session != null && session.isOpen()) {
                session.close();
            }
            USER_SESSION_CACHE.remove(String.valueOf(sessionKey));
        } catch (IOException e) {
            log.error("{}的session会话未正常关闭", sessionKey);
        }
    }

    /**
     * 获取所有的key
     * @return
     */
    public static Set<String> getKeys() {
        return USER_SESSION_CACHE.keySet();
    }

    /**
     * 获取用户session
     * @param sessionKey
     * @return
     */
    public static WebSocketSession getSession(Long sessionKey) {
        return USER_SESSION_CACHE.get(String.valueOf(sessionKey));
    }

    /**
     * 清空用户会话
     */
    public static void clear() {
        USER_SESSION_CACHE.forEach((k, v) -> {
            try {
                if (v != null && v.isOpen()) {
                    v.close();
                }
            } catch (Exception e) {
                log.warn("session会话关闭时出错", e);
            }
        });
        USER_SESSION_CACHE.clear();
    }
}
