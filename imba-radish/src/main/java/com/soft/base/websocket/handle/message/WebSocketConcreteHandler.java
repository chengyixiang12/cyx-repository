package com.soft.base.websocket.handle.message;

import com.soft.base.enums.WebSocketOrderEnum;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: websocket具体业务接口
 * @DateTime: 2024/11/22 0:14
 **/
public interface WebSocketConcreteHandler<T> {

    void handle(WebSocketSession session, AbstractWebSocketMessage<T> message) throws IOException;

    WebSocketOrderEnum getOrder();
}
