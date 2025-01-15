package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.FileTransferContinueRecParams;
import com.soft.base.websocket.send.FileTransferContinueSendParams;
import com.soft.base.websocket.send.SendParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: 文件传输续传处理器
 * @DateTime: 2025/1/2 11:11
 **/
@Slf4j
@Component
public class FileTransferContinueHandler  implements WebSocketConcreteHandler<String> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public FileTransferContinueHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();
        FileTransferContinueRecParams fileTransferContinueRecParams = JSON.parseObject(message.getPayload(), FileTransferContinueRecParams.class);
        String fileHash = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INFO + username);
        if (StringUtils.isBlank(fileHash) || !fileHash.equals(fileTransferContinueRecParams.getFileHash())) {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMessage("文件hash值不一致");
            sendParams.setOrder(fileTransferContinueRecParams.getOrder());
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            return;
        }
        Integer currIndex = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        log.info("当前索引为：{}", currIndex);
        FileTransferContinueSendParams fileTransferContinueSendParams = new FileTransferContinueSendParams();
        fileTransferContinueSendParams.setStatus(true);
        fileTransferContinueSendParams.setIndex(currIndex);
        fileTransferContinueSendParams.setOrder(fileTransferContinueRecParams.getOrder());
        session.sendMessage(new TextMessage(fileTransferContinueSendParams.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_CONTINUE;
    }
}
