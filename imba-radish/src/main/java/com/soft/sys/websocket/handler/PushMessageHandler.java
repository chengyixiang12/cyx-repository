package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.PushMessageRecParam;
import com.soft.sys.websocket.send.PushMessageSendParams;
import com.soft.sys.websocket.session.WebSocketSessionManager;
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
        PushMessageRecParam pushMessageRecParam = JSON.parseObject(message.getPayload(), PushMessageRecParam.class);
        JSONArray receivers = pushMessageRecParam.getReceivers();
        PushMessageSendParams pushMessageSendParams = new PushMessageSendParams();
        pushMessageSendParams.setMsg(pushMessageRecParam.getMessage());
        pushMessageSendParams.setMsg(pushMessageRecParam.getOrder());
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
