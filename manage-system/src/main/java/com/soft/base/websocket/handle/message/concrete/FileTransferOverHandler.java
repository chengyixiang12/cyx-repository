package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.DateUtil;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.FileTransferOverRecParams;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: cyx
 * @Description: 文件传输结束处理器
 * @DateTime: 2024/12/30 20:00
 **/
@Component
@Slf4j
public class FileTransferOverHandler implements WebSocketConcreteHandler<String> {

    private final RedisTemplate<String, Object> redisTemplate;

    private final DateUtil dateUtil;

    private final SysFileService sysFileService;

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Value(value = "${storage.big-file.location}")
    private String bigFileLocation;

    @Autowired
    public FileTransferOverHandler(RedisTemplate<String, Object> redisTemplate, DateUtil dateUtil, SysFileService sysFileService) {
        this.redisTemplate = redisTemplate;
        this.dateUtil = dateUtil;
        this.sysFileService = sysFileService;
    }
    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        FileTransferOverRecParams fileTransferOverRecParams = JSON.parseObject(message.getPayload(), FileTransferOverRecParams.class);
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        String username = userDto.getUsername();
        String fileKey = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_KEY + username);
        // 源文件名
        String originalName = fileTransferOverRecParams.getOriginalName();
        // 文件后缀
        String suffix = originalName.substring(originalName.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX));
        String objectKey = BaseConstant.LEFT_SLASH + dateUtil.date8Number() + BaseConstant.LEFT_SLASH + fileKey + suffix;

        byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
        int length = BaseConstant.BUFFER_SIZE;
        long size = BaseConstant.LONG_INIT_VAL;
        int no = BaseConstant.INTEGER_INIT_VAL;

        Integer nos = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_NO_KEY + username + BaseConstant.ENG_COLON + fileKey);
        if (nos == null) {
            throw new GlobalException("分片文件序号为空");
        }

        // 大文件存储路径
        File file = new File(bigFileLocation + objectKey);
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        try (OutputStream os = new FileOutputStream(file)) {
            while (no < nos) {
                String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.LEFT_SLASH + no + BaseConstant.TMP_SUFFIX;
                Path path = Paths.get(filePath);
                InputStream stream = Files.newInputStream(path);
                length = stream.read(buffer, BaseConstant.INTEGER_INIT_VAL, length);
                log.info("读取文件长度为：{}", length);
                stream.close();
                os.write(buffer, BaseConstant.INTEGER_INIT_VAL, length);
                size += length;
                no++;
            }

//            LocalDateTime now = LocalDateTime.now();
//            SysFile sysFile = new SysFile();
//            sysFile.setFileSize(size);
//            sysFile.setCreateBy(username);
//            sysFile.setUpdateBy(username);
//            sysFile.setOriginalName(originalName);
//            sysFile.setCreateTime(now);
//            sysFile.setUpdateTime(now);
//            sysFile.setObjectKey(objectKey);
//            sysFile.setFileSuffix(suffix);
//            sysFile.setLocation(BaseConstant.DISK_STORAGE_LOCATION);
//            sysFile.setFileKey(fileKey);
//            sysFile.setDelFlag(BaseConstant.DEL_FLAG_EXIST);
//            sysFileService.save(sysFile);
//            log.info("start save file data to database...");

            log.info("start remove cache...");
            File tmpFile = new File(tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey);
            tmpFile.deleteOnExit();
//            redisTemplate.delete(RedisConstant.SLICE_FILE_KEY + username);
//            log.info("删除{}的分片文件key缓存", username);
//            redisTemplate.delete(RedisConstant.SLICE_FILE_NO_KEY + username + BaseConstant.ENG_COLON + fileKey);
//            log.info("删除{}的分片文件no缓存", username);

            SendParams sendParams = new SendParams();
            sendParams.setStatus(true);
            session.sendMessage(new TextMessage(sendParams.toString()));
        }
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_OVER;
    }
}
