package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.PushMessageRecParams;
import com.soft.base.websocket.send.PushMessageSendParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: 推送消息处理器
 * @DateTime: 2024/11/22 10:01
 **/

@Component
@Slf4j
public class PushMessageHandler implements WebSocketConcreteHandler<String> {
    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        PushMessageRecParams pushMessageRecParams = JSON.parseObject(message.getPayload(), PushMessageRecParams.class);
        JSONArray receivers = pushMessageRecParams.getReceivers();
        PushMessageSendParams pushMessageSendParams = new PushMessageSendParams();
        pushMessageSendParams.setMessage(pushMessageRecParams.getMessage());
        pushMessageSendParams.setMessage(pushMessageRecParams.getOrder());
        for (int i = 0; i < receivers.size(); i++) {
            WebSocketSession sendSession = WebSocketSessionManager.getSession(receivers.getLong(i));
            if (sendSession == null) {
                continue;
            }
            sendSession.sendMessage(new TextMessage(pushMessageSendParams.toJsonString()));
        }
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.PUSH_MESSAGE;
    }
}
