package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.FileTransferContinueRequest;
import com.soft.sys.websocket.send.FileTransferContinueResponse;
import com.soft.sys.websocket.send.WebSocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public FileTransferContinueHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();
        FileTransferContinueRequest fileTransferContinueRecParam = JSON.parseObject(message.getPayload(), FileTransferContinueRequest.class);
        String fileHash = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INFO + username);
        if (StringUtils.isBlank(fileHash) || !fileHash.equals(fileTransferContinueRecParam.getFileHash())) {
            WebSocketResponse sendParams = new WebSocketResponse();
            sendParams.setStatus(false);
            sendParams.setMsg("文件hash值不一致");
            sendParams.setOrder(fileTransferContinueRecParam.getOrder());
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            return;
        }
        Integer currIndex = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        log.info("当前索引为：{}", currIndex);
        FileTransferContinueResponse fileTransferContinueSendParams = new FileTransferContinueResponse();
        fileTransferContinueSendParams.setStatus(true);
        fileTransferContinueSendParams.setIndex(currIndex);
        fileTransferContinueSendParams.setOrder(fileTransferContinueRecParam.getOrder());
        session.sendMessage(new TextMessage(fileTransferContinueSendParams.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_CONTINUE;
    }
}
