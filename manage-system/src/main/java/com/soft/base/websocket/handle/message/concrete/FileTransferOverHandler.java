package com.soft.base.websocket.handle.message.concrete;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.dto.UserDto;
import com.soft.base.entity.SysFile;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.DateUtil;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.receive.FileTransferOverRecParams;
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
import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
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
        String fileKey = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_KEY + userDto.getUsername());
        String filePath = tmpPath + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.TMP_SUFFIX;
        LocalDateTime now = LocalDateTime.now();
        String originalName = fileTransferOverRecParams.getOriginalName();
        String suffix = originalName.substring(originalName.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX));
        String objectKey = BaseConstant.LEFT_SLASH + dateUtil.date8Number() + BaseConstant.LEFT_SLASH + fileKey + suffix;
        File file = new File(bigFileLocation + objectKey);
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        Path path = Paths.get(filePath);
        byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
        int length = BaseConstant.BUFFER_SIZE;
        long size = BaseConstant.LONG_INIT_VAL;
        try (InputStream stream = Files.newInputStream(path);
        OutputStream os = new FileOutputStream(file)) {
            while (BaseConstant.FILE_OVER_SIGN != (length = stream.read(buffer, BaseConstant.INTEGER_INIT_VAL, length))) {
                os.write(buffer, BaseConstant.INTEGER_INIT_VAL, length);
            }
            os.flush();
            size += length;
        } finally {
            log.info("start save file data to database...");
            SysFile sysFile = new SysFile();
            sysFile.setFileSize(size);
            sysFile.setCreateBy(userDto.getUsername());
            sysFile.setUpdateBy(userDto.getUsername());
            sysFile.setOriginalName(originalName);
            sysFile.setCreateTime(now);
            sysFile.setUpdateTime(now);
            sysFile.setObjectKey(objectKey);
            sysFile.setFileSuffix(suffix);
            sysFile.setLocation(BaseConstant.DISK_STORAGE_LOCATION);
            sysFile.setFileKey(fileKey);
            sysFile.setDelFlag(BaseConstant.DEL_FLAG_EXIST);
            sysFileService.save(sysFile);

            log.info("start remove cache...");
            File tmpFile = new File(filePath);
            tmpFile.deleteOnExit();
            redisTemplate.delete(RedisConstant.SLICE_FILE_KEY + userDto.getUsername());

            session.sendMessage(new TextMessage(JSON.toJSONString(R.ok())));
        }
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_OVER;
    }
}
