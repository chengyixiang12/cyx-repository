package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.utils.UniversalUtil;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.FileTransferStartRecParams;
import com.soft.base.websocket.send.FileTransferStartSendParams;
import com.soft.base.websocket.send.SendParams;
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
import java.util.HashMap;
import java.util.Map;

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
    public FileTransferStartHandler(UniversalUtil universalUtil,
                                    RedisTemplate<String, Object> redisTemplate) {
        this.universalUtil = universalUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();
        FileTransferStartRecParams fileTransferStartRecParams = JSON.parseObject(message.getPayload(), FileTransferStartRecParams.class);
        String fileKey = universalUtil.fileKeyGen();
        log.info("fileKey: {}", fileKey);
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey;
        File file = new File(filePath);
        boolean isCreate = file.mkdirs();
        if (!isCreate) {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMessage("文件夹创建失败");
            session.sendMessage(new TextMessage(sendParams.toString()));
            log.info("文件夹创建失败，{}", filePath);
            return;
        }

        log.info("文件夹创建成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_KEY + username, fileKey);
        log.info("分片文件key缓存成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_INDEX_KEY + username, BaseConstant.INTEGER_INIT_VAL);
        log.info("分片文件索引缓存成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_INFO + username, fileTransferStartRecParams.getFileHash());
        log.info("分片文件hash缓存成功");
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_START;
    }
}
