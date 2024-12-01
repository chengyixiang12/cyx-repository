package com.soft.base.async;

import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysFile;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author: cyx
 * @Description: 异步上传文件（并未实现真正的异步，可能是因为文件接口的原因）
 * @DateTime: 2024/11/30 21:42
 **/

@Component
public class FileUploadAsync {

    private final SysFileMapper sysFileMapper;

    private final MinioUtil minioUtil;

    @Autowired
    public FileUploadAsync(SysFileMapper sysFileMapper, MinioUtil minioUtil) {
        this.sysFileMapper = sysFileMapper;
        this.minioUtil = minioUtil;
    }

    /**
     * 异步上传文件
     * @param inputStream
     * @param fileSuffix
     * @param fileSize
     * @param originalFilename
     */
    @Async
    public void uploadFile(InputStream inputStream, String fileSuffix, Long fileSize, String originalFilename) {
        String fileKey = minioUtil.fileKeyGen();
        String objectKey = minioUtil.upload(inputStream, fileKey, fileSuffix, fileSize);
        SysFile sysFile = new SysFile();
        sysFile.setFileKey(fileKey);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setLocation(BaseConstant.DEFAULT_STORAGE_LOCATION);
        sysFile.setObjectKey(objectKey);
        sysFile.setOriginalName(originalFilename);
        sysFile.setFileSize(fileSize);
        sysFileMapper.insert(sysFile);
    }
}
