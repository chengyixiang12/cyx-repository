package com.soft.base.core.listener;

import com.soft.base.websocket.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2025/8/17
 * @description 服务关闭事件监听器
 */

@Component
@Slf4j
public class ApplicationShutdownListener implements ApplicationListener<ContextClosedEvent>, Ordered {

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        // 在应用上下文关闭时，首先关闭 WebSocket 连接
        try {
            log.info("开始清理 WebSocket 连接...");
            WebSocketSessionManager.clear();
        } catch (Exception e) {
            log.error("关闭 WebSocket 连接时出错", e);
        }
    }

    @Override
    public int getOrder() {
        // 确保在其他销毁操作之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

