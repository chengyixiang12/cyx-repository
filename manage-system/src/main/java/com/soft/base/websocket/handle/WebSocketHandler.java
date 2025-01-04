package com.soft.base.websocket.handle;

import com.alibaba.fastjson2.JSON;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.OrderReceiveParams;
import com.soft.base.websocket.send.SendParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获取消息体
        String payload = message.getPayload();
        OrderReceiveParams orderReceiveParams = JSON.parseObject(payload, OrderReceiveParams.class);
        String order = orderReceiveParams.getOrder();
        if (StringUtils.isBlank(order)) {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMessage("websocket连接异常，指令为空");
            log.error("websocket连接异常，指令为空");
            return;
        }

        @SuppressWarnings("unchecked")
        WebSocketConcreteHandler<String> webSocketConcreteHandler = (WebSocketConcreteHandler<String>) WebSocketConcreteHolder.getConcreteHandler(order);
        webSocketConcreteHandler.handle(session, message);

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
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
