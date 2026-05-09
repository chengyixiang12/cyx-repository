package com.soft.base.websocket;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: websocket连接会话管理
 * @DateTime: 2024/11/21 23:26
 **/

@Slf4j
public class WebSocketSessionManager {

    // 默认缓存30秒过期
    private static final TimedCache<String, WebSocketSession> USER_SESSION_CACHE = CacheUtil.newTimedCache(30000);

    static {
        // 启动定时清理，每10秒清理一次
        USER_SESSION_CACHE.schedulePrune(10000);
    }

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
     * 获取用户session
     * @param sessionKey
     * @return
     */
    public static WebSocketSession getSession(Long sessionKey) {
        return USER_SESSION_CACHE.get(String.valueOf(sessionKey), false);
    }

    /**
     * 清空用户会话
     */
    public static void clear() {
        USER_SESSION_CACHE.forEach(item -> {
            try {
                if (item != null && item.isOpen()) {
                    item.close();
                }
            } catch (Exception e) {
                log.warn("session会话关闭时出错", e);
            }
        });
        USER_SESSION_CACHE.clear();
    }
}
