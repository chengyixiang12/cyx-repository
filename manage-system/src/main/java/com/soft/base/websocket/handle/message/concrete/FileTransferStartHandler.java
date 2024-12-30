package com.soft.base.websocket.handle.message.concrete;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.utils.UniversalUtil;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.send.FileTransferStartSendParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;

/**
 * @Author: cyx
 * @Description: 文件传输开始处理器
 * @DateTime: 2024/12/30 15:49
 **/

@Component
@Slf4j
public class FileTransferStartHandler implements WebSocketConcreteHandler<String> {

    private final UniversalUtil universalUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Autowired
    public FileTransferStartHandler(UniversalUtil universalUtil, RedisTemplate<String, Object> redisTemplate) {
        this.universalUtil = universalUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        log.info("username: {}", userDto.getUsername());
        FileTransferStartSendParams fileTransferStartSendParams = new FileTransferStartSendParams();
        String fileKey = universalUtil.fileKeyGen();
        log.info("fileKey: {}", fileKey);
        fileTransferStartSendParams.setFileKey(fileKey);
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.TXT_SUFFIX;
        File file = new File(filePath);
        boolean isCreate = file.createNewFile();
        if (isCreate) {
            log.info("文件创建成功");
        } else {
            log.info("文件创建失败");
        }
        fileTransferStartSendParams.setIsCreate(isCreate);
        session.sendMessage(new TextMessage(fileTransferStartSendParams.toJsonString()));
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_KEY + userDto.getUsername(), fileKey);
        log.info("file transfer start...");
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_START;
    }
}
