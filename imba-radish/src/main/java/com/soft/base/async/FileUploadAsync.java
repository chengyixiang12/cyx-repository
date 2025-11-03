package com.soft.base.async;

import com.soft.base.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/10 9:32
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUploadAsync {

    public final MinioUtil minioUtil;

    @Async
    public void upload(InputStream is, String bucket, Long fileSize, String objectKey) {
        minioUtil.upload(is, bucket, fileSize, objectKey);;
    }

    @Async
    public void upload(InputStream is, Long fileSize, String objectKey) {
        minioUtil.upload(is, fileSize, objectKey);;
    }
}
