package com.soft.sys.websocket.handler;

import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.send.PingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 心跳发起处理类
 * @author cyx
 * @date 2026-06-18
 */
@Component
@Slf4j
public class PingHandler implements WebSocketConcreteHandler<String> {

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        PingResponse pingSendParams = new PingResponse();
        pingSendParams.setOrder(WebSocketOrderEnum.PING.toString());
        pingSendParams.setMsg(message.getPayload());
        session.sendMessage(new TextMessage(pingSendParams.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.PING;
    }
}
