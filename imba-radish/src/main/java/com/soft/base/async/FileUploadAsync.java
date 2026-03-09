package com.soft.base.async;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soft.base.entity.SysFile;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.utils.MinioUtil;
import com.soft.base.utils.UniversalUtil;
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

    private final UniversalUtil universalUtil;

    private final SysFileMapper sysFileMapper;

    @Async
    public void fileUpload(File fileTemp, String objectKey, long fileSize, Long sysFileId) {
        try (InputStream fileHashIs = Files.newInputStream(fileTemp.toPath())) {
            String fileHash = universalUtil.generateFileHash(fileHashIs);
            SysFile sysFile = sysFileMapper.selectOne(Wrappers.<SysFile>lambdaQuery()
                    .eq(SysFile::getFileHash, fileHash)
                    .select(SysFile::getFileKey, SysFile::getBucket, SysFile::getObjectKey));
            if (sysFile != null) {
                sysFile.setId(sysFileId);
                sysFileMapper.updateById(sysFile);
            } else {
                try (InputStream uploadIs = Files.newInputStream(fileTemp.toPath())) {
                    minioUtil.upload(uploadIs, fileSize, objectKey);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            fileTemp.delete();
            fileTemp.getParentFile().delete();
        }
    }
}
