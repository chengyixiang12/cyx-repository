package com.soft.sys.websocket.handler;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.WebSocketConstant;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.FileTransferStartRequest;
import com.soft.sys.websocket.send.WebSocketResponse;
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

    private final RedisTemplate<String, Object> redisTemplate;

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Autowired
    public FileTransferStartHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();
        FileTransferStartRequest fileTransferStartRecParam = JSON.parseObject(message.getPayload(), FileTransferStartRequest.class);
        String fileKey = IdUtil.fastSimpleUUID();
        log.info("fileKey: {}", fileKey);
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey;
        File file = new File(filePath);
        boolean isCreate = file.mkdirs();
        WebSocketResponse sendParams = new WebSocketResponse();
        if (!isCreate) {

            sendParams.setStatus(false);
            sendParams.setMsg("文件夹创建失败");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            log.info("文件夹创建失败，{}", filePath);
            return;
        }

        log.info("文件夹创建成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_KEY + username, fileKey);
        log.info("分片文件key缓存成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_INDEX_KEY + username, BaseConstant.INTEGER_INIT_VAL);
        log.info("分片文件索引缓存成功");
        redisTemplate.opsForValue().set(RedisConstant.SLICE_FILE_INFO + username, fileTransferStartRecParam.getFileHash());
        log.info("分片文件hash缓存成功");

        sendParams.setStatus(true);
        sendParams.setMsg("文件夹创建成功");
        session.sendMessage(new TextMessage(sendParams.toJsonString()));
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_START;
    }
}
