package com.soft.sys.websocket.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: cyx
 * @Description: websocket连接会话管理
 *
 * <p>会话 key 为 {@code userId:deviceType}，同一用户的不同设备类型（web/app/mini-program）
 * 可以同时保持连接。</p>
 *
 * @DateTime: 2024/11/21 23:26
 **/

@Slf4j
public class WebSocketSessionManager {

    private static final Map<String, WebSocketSession> USER_SESSION_CACHE = new ConcurrentHashMap<>();

    private static final String KEY_SEPARATOR = ":";

    /**
     * 生成会话 key
     */
    public static String sessionKey(Long userId, String deviceType) {
        return userId + KEY_SEPARATOR + deviceType;
    }

    /**
     * 从会话 key 解析用户 ID
     */
    public static Long parseUserId(String sessionKey) {
        int index = sessionKey.indexOf(KEY_SEPARATOR);
        return Long.parseLong(index > 0 ? sessionKey.substring(0, index) : sessionKey);
    }

    /**
     * 从会话 key 解析设备类型
     */
    public static String parseDeviceType(String sessionKey) {
        int index = sessionKey.indexOf(KEY_SEPARATOR);
        return index > 0 ? sessionKey.substring(index + 1) : null;
    }

    /**
     * 添加用户指定设备类型的 session
     */
    public static void addSession(Long userId, String deviceType, WebSocketSession session) {
        USER_SESSION_CACHE.put(sessionKey(userId, deviceType), session);
    }

    /**
     * 获取用户指定设备类型的 session
     */
    public static WebSocketSession getSession(Long userId, String deviceType) {
        return USER_SESSION_CACHE.get(sessionKey(userId, deviceType));
    }

    /**
     * 获取用户所有在线设备的 session
     */
    public static List<WebSocketSession> getSessions(Long userId) {
        String prefix = userId + KEY_SEPARATOR;
        List<WebSocketSession> sessions = new ArrayList<>();
        USER_SESSION_CACHE.forEach((key, session) -> {
            if (key.startsWith(prefix)) {
                sessions.add(session);
            }
        });
        return sessions;
    }

    /**
     * 用户是否有任意设备在线
     */
    public static boolean isOnline(Long userId, String deviceType) {
        String sessionKey = userId + KEY_SEPARATOR + deviceType;
        return USER_SESSION_CACHE.keySet().stream().anyMatch(key -> key.equals(sessionKey));
    }

    /**
     * 移除用户指定设备类型的 session
     */
    public static void removeSession(Long userId, String deviceType) {
        removeByKey(sessionKey(userId, deviceType));
    }

    /**
     * 移除用户所有设备的 session
     */
    public static void removeAll(Long userId) {
        String prefix = userId + KEY_SEPARATOR;
        USER_SESSION_CACHE.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .toList()
                .forEach(WebSocketSessionManager::removeByKey);
    }

    private static void removeByKey(String key) {
        WebSocketSession session = USER_SESSION_CACHE.remove(key);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("{}的session会话未正常关闭", key);
            }
        }
    }

    /**
     * 获取所有的key
     *
     * @return key 格式为 userId:deviceType
     */
    public static Set<String> getKeys() {
        return USER_SESSION_CACHE.keySet();
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
