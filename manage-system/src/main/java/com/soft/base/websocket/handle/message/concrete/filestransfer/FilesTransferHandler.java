package com.soft.base.websocket.handle.message.concrete.filestransfer;

import com.alibaba.fastjson2.JSON;
import com.soft.base.async.FilesTransferAsync;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.FilesTransferRecParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/10 10:44
 **/

@Component
@Slf4j
public class FilesTransferHandler implements WebSocketConcreteHandler<String> {

    private final FilesTransferAsync filesTransferAsync;

    @Autowired
    public FilesTransferHandler(FilesTransferAsync filesTransferAsync) {
        this.filesTransferAsync = filesTransferAsync;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        FilesTransferRecParams filesTransferRecParams = JSON.parseObject(message.getPayload(), FilesTransferRecParams.class);
        //TODO 待定
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILES_TRANSFER;
    }
}
