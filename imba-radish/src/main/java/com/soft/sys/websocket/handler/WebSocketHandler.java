package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import com.soft.sys.websocket.receive.AbstractRecParam;
import com.soft.sys.websocket.receive.RecParam;
import com.soft.sys.websocket.send.SendParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: cyx
 * @Description: 负责处理消息
 * @DateTime: 2024/11/21 19:46
 **/
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        // 获取消息体
        String payload = message.getPayload();
        AbstractRecParam abstractRecParam = JSON.parseObject(payload, RecParam.class);
        String order = abstractRecParam.getOrder();
        if (StringUtils.isBlank(order)) {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMsg("websocket连接异常，指令为空");
            log.warn("websocket连接异常，指令为空");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            return;
        }
        if (!WebSocketOrderEnum.exist(order)) {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMsg("无效的指令");
            log.warn("无效的指令");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            return;
        }

        @SuppressWarnings("unchecked")
        WebSocketConcreteHandler<String> webSocketConcreteHandler = (WebSocketConcreteHandler<String>) WebSocketConcreteHolder.getConcreteHandler(order);
        webSocketConcreteHandler.handle(session, message);

    }

    @Override
    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) {
        try {
            log.info("received file, start process...");
            @SuppressWarnings("unchecked")
            WebSocketConcreteHandler<ByteBuffer> webSocketConcreteHandler = (WebSocketConcreteHandler<ByteBuffer>) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.FILE_TRANSFER.toString());
            webSocketConcreteHandler.handle(session, message);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
