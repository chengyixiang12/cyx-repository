package com.soft.base.websocket.handle.message.concrete;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.send.SendParams;
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
        String username = userDto.getUsername();
        // 分片文件key
        String fileKey = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_KEY + username);
        // 分片文件索引
        Integer index = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.LEFT_SLASH + index + BaseConstant.TMP_SUFFIX;
        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
            log.error("文件创建失败，{}", file.getName());
            SendParams sendParams = new SendParams();
            sendParams.setStatus(false);
            sendParams.setMessage("文件创建失败");
            session.sendMessage(new TextMessage(sendParams.toString()));
            return;
        }
        int byteIndex = BaseConstant.INTEGER_INIT_VAL;
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            while (byteIndex < payload.array().length) {
                os.write(payload.array(), byteIndex, BaseConstant.BUFFER_SIZE);
                byteIndex += BaseConstant.BUFFER_SIZE;
            }
            os.flush();
            log.info("分片文件保存成功");
        } finally {
            SendParams sendParams = new SendParams();
            sendParams.setStatus(true);
            session.sendMessage(new TextMessage(sendParams.toString()));
            redisTemplate.opsForValue().increment(RedisConstant.SLICE_FILE_INDEX_KEY + username);
            log.info("分片文件索引递增");
        }

    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER;
    }
}
