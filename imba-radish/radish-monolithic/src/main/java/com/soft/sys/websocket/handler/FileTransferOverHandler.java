package com.soft.sys.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.soft.common.constants.BaseConstant;
import com.soft.common.constants.RedisConstant;
import com.soft.common.constants.WebSocketConstant;
import com.soft.sys.entity.SysFile;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.properties.MinioProperty;
import com.soft.sys.service.SysFileService;
import com.soft.sys.utils.FileNameUtil;
import com.soft.sys.utils.MinioUtil;
import com.soft.sys.websocket.api.WebSocketConcreteHandler;
import com.soft.sys.websocket.receive.FileTransferOverRequest;
import com.soft.sys.websocket.send.WebSocketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件传输结束处理器。
 *
 * <p>统一以 MinIO 作为文件存储介质：分片只是传输过程中的临时文件，按序拼接后直接
 * 流式上传到 MinIO，不再落地到本地「大文件存储」目录。上传成功并确认对象存在后才落库，
 * 失败则保留临时分片供重试。</p>
 *
 * @Author: cyx
 * @Description: 文件传输结束处理器
 * @DateTime: 2024/12/30 20:00
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class FileTransferOverHandler implements WebSocketConcreteHandler<String> {

    @Value(value = "${tmp.path}")
    private String tmpPath;

    private final RedisTemplate<String, Object> redisTemplate;

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;

    private final MinioProperty minioProperty;

    @Override
    public void handle(WebSocketSession session, AbstractWebSocketMessage<String> message) throws IOException {
        FileTransferOverRequest request = JSON.parseObject(message.getPayload(), FileTransferOverRequest.class);
        UserDTO userDto = (UserDTO) session.getAttributes().get(WebSocketConstant.WEBSOCKET_USER);
        Long userId = userDto.getId();
        String username = userDto.getUsername();
        String fileKey = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_KEY + username);
        String fileHash = (String) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INFO + username);
        String originalName = FileNameUtil.sanitize(request.getOriginalName());
        // 文件名可能没有后缀，避免 lastIndexOf 返回 -1 导致下标越界
        int dot = originalName.lastIndexOf(".");
        String suffix = dot >= 0 ? originalName.substring(dot) : "";

        WebSocketResponse sendParams = new WebSocketResponse();
        sendParams.setStatus(false);
        sendParams.setOrder(request.getOrder());

        Integer maxIndex = (Integer) redisTemplate.opsForValue().get(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        if (fileKey == null || maxIndex == null || maxIndex <= 0) {
            sendParams.setMsg("分片文件信息为空");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            log.warn("分片信息为空，用户：{}，文件：{}", username, originalName);
            return;
        }

        Path sliceDir = Path.of(tmpPath, username, fileKey);
        // objectKey 用 / 分隔，由 MinioUtil 统一生成，不能用 File.separator（Windows 下会写成反斜杠）
        String objectKey = minioUtil.getObjectKey(fileKey, suffix);

        // 按序拼接全部分片；MinIO putObject 需要明确对象大小，因此先累加各分片长度
        List<InputStream> streams = new ArrayList<>(maxIndex);
        long size = 0L;
        try {
            for (int i = 0; i < maxIndex; i++) {
                Path part = sliceDir.resolve(i + BaseConstant.TMP_SUFFIX);
                if (!Files.isRegularFile(part)) {
                    sendParams.setMsg("分片缺失，索引：" + i);
                    session.sendMessage(new TextMessage(sendParams.toJsonString()));
                    log.warn("分片缺失：{}", part);
                    return;
                }
                size += Files.size(part);
                streams.add(Files.newInputStream(part));
            }

            try (InputStream is = new SequenceInputStream(Collections.enumeration(streams))) {
                minioUtil.upload(is, size, objectKey);
            }
            if (!minioUtil.exists(objectKey)) {
                minioUtil.delete(objectKey);
                throw new IllegalStateException("对象存储校验失败");
            }
        } catch (Exception e) {
            closeQuietly(streams);
            log.error("文件写入 MinIO 失败，objectKey={}", objectKey, e);
            sendParams.setMsg("文件写入存储失败，请重试");
            session.sendMessage(new TextMessage(sendParams.toJsonString()));
            return;
        }

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
        sysFile.setLocation(BaseConstant.Minio.MINIO);
        sysFile.setBucket(minioProperty.getDefaultBucket());
        sysFile.setFileKey(fileKey);
        sysFile.setFileHash(fileHash);
        sysFile.setDelFlag(BaseConstant.LogicDelete.EXIST);
        sysFileService.save(sysFile);

        redisTemplate.delete(RedisConstant.SLICE_FILE_KEY + username);
        redisTemplate.delete(RedisConstant.SLICE_FILE_INDEX_KEY + username);
        redisTemplate.delete(RedisConstant.SLICE_FILE_INFO + username);
        deleteRecursively(sliceDir);

        sendParams.setStatus(true);
        session.sendMessage(new TextMessage(sendParams.toJsonString()));
    }

    /** 打开分片流的中途失败时，逐个兜底关闭 */
    private void closeQuietly(List<InputStream> streams) {
        for (InputStream stream : streams) {
            try {
                stream.close();
            } catch (IOException ignored) {
                // 忽略关闭异常
            }
        }
    }

    private void deleteRecursively(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除临时分片失败：{}", p, e);
                }
            });
        } catch (IOException e) {
            log.warn("遍历临时分片目录失败：{}", path, e);
        }
    }

    @Override
    public WebSocketOrderEnum getOrder() {
        return WebSocketOrderEnum.FILE_TRANSFER_OVER;
    }
}
