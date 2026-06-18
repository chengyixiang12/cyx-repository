package com.soft.sys.async;

import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import com.soft.sys.websocket.handler.PingHandler;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 *
 * @author cyx
 * @date 2026-06-18
 */
@Component
@Slf4j
public class SendPingAsync {

    @Async
    public void send(String sessionKey) {
        WebSocketSession session = WebSocketSessionManager.getSession(Long.parseLong(sessionKey));
        PingHandler pingHandler = (PingHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.PING.toString());
        try {
            pingHandler.handle(session, new TextMessage("ping"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
