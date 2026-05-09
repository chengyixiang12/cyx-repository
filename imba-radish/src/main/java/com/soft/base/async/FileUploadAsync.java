package com.soft.base.async;

import com.soft.base.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/2/10 9:32
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUploadAsync {

    private final MinioUtil minioUtil;

    @Async
    public void fileUpload(File fileTemp, String objectKey, long fileSize) {
        try (InputStream uploadIs = Files.newInputStream(fileTemp.toPath())) {
            minioUtil.upload(uploadIs, fileSize, objectKey);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            fileTemp.delete();
            fileTemp.getParentFile().delete();
        }
    }
}
