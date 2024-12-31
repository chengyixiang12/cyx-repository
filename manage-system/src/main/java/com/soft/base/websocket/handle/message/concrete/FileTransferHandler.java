package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.resultapi.R;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @Author: cyx
 * @Description: 文件传输处理器
 * @DateTime: 2024/12/30 18:16
 **/

@Slf4j
@Component
public class FileTransferHandler implements WebSocketConcreteHandler<ByteBuffer> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Autowired
    public FileTransferHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<ByteBuffer> message) throws IOException {
        ByteBuffer payload = message.getPayload();
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String fileKey = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_KEY + userDto.getUsername());
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.TMP_SUFFIX;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException();
        }
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            os.write(payload.array());
            os.flush();
        } finally {
            session.sendMessage(new TextMessage(JSON.toJSONString(R.ok())));
        }

    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER;
    }
}
