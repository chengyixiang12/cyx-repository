package com.soft.base.websocket.handle.message.concrete;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.exception.GlobalException;
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
        String username = userDto.getUsername();
        FileTransferStartSendParams fileTransferStartSendParams = new FileTransferStartSendParams();
        String fileKey = universalUtil.fileKeyGen();
        log.info("fileKey: {}", fileKey);
        fileTransferStartSendParams.setFileKey(fileKey);
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey;
        File file = new File(filePath);
        boolean isCreate = file.mkdirs();
        if (isCreate) {
            log.info("文件夹创建成功");
            fileTransferStartSendParams.setIsCreate(true);
            session.sendMessage(new TextMessage(fileTransferStartSendParams.toString()));
            redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_KEY + username, fileKey);
            log.info("分片文件key缓存成功");
            redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_INDEX_KEY + username, BaseConstant.INTEGER_INIT_VAL);
            log.info("分片文件序号缓存成功");
        } else {
            log.info("文件夹创建失败");
            throw new GlobalException("文件夹创建失败");
        }
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_START;
    }
}
