package com.soft.sys.schedule;

import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import com.soft.sys.websocket.handler.PingHandler;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

/**
 * 心跳
 * @author cyx
 * @date 2026-06-18
 */
@Component
@Slf4j
public class WebSocketHeardHeatTimer {

    @Scheduled(cron = "*/30 * * * * *")
    public void run() {
        Set<String> keys = WebSocketSessionManager.getKeys();
        keys.forEach(item -> {
            WebSocketSession session = WebSocketSessionManager.getSession(Long.parseLong(item));
            PingHandler pingHandler = (PingHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.PING.toString());
            try {
                pingHandler.handle(session, new TextMessage("ping"));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
