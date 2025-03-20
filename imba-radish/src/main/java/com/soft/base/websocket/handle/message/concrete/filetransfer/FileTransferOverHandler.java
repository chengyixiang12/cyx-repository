package com.soft.base.websocket.handle.message.concrete.filetransfer;

import com.alibaba.fastjson2.JSON;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.WebSocketConstant;
import com.soft.base.model.dto.UserDto;
import com.soft.base.entity.SysFile;
import com.soft.base.enums.WebSocketOrderEnum;
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
import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 文件传输结束处理器
 * @DateTime: 2024/12/30 20:00
 **/
@Component
@Slf4j
public class FileTransferOverHandler implements WebSocketConcreteHandler<String> {

    @Value(value = "${tmp.path}")
    private String tmpPath;

    @Value(value = "${storage.big-file.location}")
    private String bigfileLocation;

    private final RedisTemplate<String, Object> redisTemplate;

    private final DateUtil dateUtil;

    private final SysFileService sysFileService;

    @Autowired
    public FileTransferOverHandler(RedisTemplate<String, Object> redisTemplate,
                                   DateUtil dateUtil,
                                   SysFileService sysFileService) {
        this.redisTemplate = redisTemplate;
        this.dateUtil = dateUtil;
        this.sysFileService = sysFileService;
    }
    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        FileTransferOverRecParams fileTransferOverRecParams = JSON.parseObject(message.getPayload(), FileTransferOverRecParams.class);
        UserDto userDto = (UserDto) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        Long userId = userDto.getId();
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
        int index = BaseConstant.INTEGER_INIT_VAL;

        SendParams sendParams = new SendParams();
        sendParams.setStatus(false);
        sendParams.setOrder(fileTransferOverRecParams.getOrder());
        Integer maxIndex = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        if (maxIndex == null) {
            sendParams.setMessage("分片文件索引为空");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            log.info("分片文件索引为空，{}", originalName);
            return;
        }

        // 大文件存储路径
        File file = new File(bigfileLocation + objectKey);
        if (!file.exists()) {
            if (file.getParentFile() != null && !file.getParentFile().mkdirs()) {
                sendParams.setMessage("文件夹创建失败");
                log.error("文件夹创建失败，{}", file.getParent());
                return;
            }
            if (!file.createNewFile()) {
                sendParams.setMessage("文件创建失败");
                log.error("文件创建失败，{}", file.getName());
                return;
            }
        }
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            while (index < maxIndex) {
                String filePath = tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey + BaseConstant.LEFT_SLASH + index + BaseConstant.TMP_SUFFIX;
                Path path = Paths.get(filePath);
                InputStream stream = Files.newInputStream(path);
                length = stream.read(buffer, BaseConstant.INTEGER_INIT_VAL, length);
                stream.close();
                os.write(buffer, BaseConstant.INTEGER_INIT_VAL, length);
                size += length;
                index++;
            }
            os.flush();
            log.info("文件写入完成，{}", file.getName());

            LocalDateTime now = LocalDateTime.now();
            SysFile sysFile = new SysFile();
            sysFile.setFileSize(size);
            sysFile.setCreateBy(userId);
            sysFile.setUpdateBy(userId);
            sysFile.setOriginalName(originalName);
            sysFile.setCreateTime(now);
            sysFile.setUpdateTime(now);
            sysFile.setObjectKey(objectKey);
            sysFile.setFileSuffix(suffix);
            sysFile.setLocation(BaseConstant.DISK_STORAGE_LOCATION);
            sysFile.setFileKey(fileKey);
            sysFile.setDelFlag(BaseConstant.DEL_FLAG_EXIST);
            sysFileService.save(sysFile);
            log.info("start save file data to database...");

            log.info("start remove cache...");
            redisTemplate.delete(RedisConstant.SLICE_FILE_KEY + username);
            log.info("删除{}的分片文件key缓存", username);
            redisTemplate.delete(RedisConstant.SLICE_FILE_INDEX_KEY + username);
            log.info("删除{}的分片文件index缓存", username);
            redisTemplate.delete(RedisConstant.SLICE_FILE_INFO + username);
            log.info("删除{}的文件hash", username);

            sendParams.setStatus(true);
            session.sendMessage(new TextMessage(sendParams.toJsonString()));

            File tmpFile = new File(tmpPath + BaseConstant.LEFT_SLASH + username + BaseConstant.LEFT_SLASH + fileKey);
            if (deleteChildFile(tmpFile)) {
                tmpFile.deleteOnExit();
            }
        }
    }

    /**
     * 递归删除文件夹中的文件
     * @param folder
     * @return
     */
    private boolean deleteChildFile(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                     if (!deleteChildFile(file)) {
                         return false;
                     }
                }
            }
        }
        return folder.delete();
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_OVER;
    }
}
