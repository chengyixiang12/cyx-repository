package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import com.soft.base.exception.GlobalException;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author: cyx
 * @Description: minio工具类
 * @DateTime: 2024/10/26 15:23
 **/

@Component
@Slf4j
public class MinioUtil {

    private final MinioClient minioClient;

    private final DateUtil dateUtil;

    @Value(value = "${minio.bucket}")
    private String bucket;

    @Autowired
    public MinioUtil(MinioClient minioClient, DateUtil dateUtil) {
        this.minioClient = minioClient;
        this.dateUtil = dateUtil;
    }

    /**
     * 生成桶路径
     * @param fileKey 文件名
     * @param fileSuffix 文件后缀
     * @return
     */
    private String getObjectKey(String fileKey, String fileSuffix) {
        return dateUtil.date8Number() + BaseConstant.LEFT_SLASH + fileKey + fileSuffix;
    }

    /**
     * 上传
     * @param is 输入流
     * @param fileKey 文件名
     * @param fileSuffix 文件后缀
     * @param fileSize 文件大小 B
     * @return
     * @throws GlobalException
     */
    public String upload(InputStream is, String fileKey, String fileSuffix, Long fileSize) throws GlobalException {
        String objectKey = getObjectKey(fileKey, fileSuffix);
        try {
            if (!existBucket()) {
                createBucket();
            }
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(is, fileSize, fileSize < BaseConstant.BURST_SIZE ? BaseConstant.BURST_FALSE : BaseConstant.BURST_SIZE)
                    .build());
            return objectKey;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 判断桶是否存在
     * @return
     * @throws GlobalException
     */
    private Boolean existBucket() throws GlobalException {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 创建桶
     * @throws GlobalException
     */
    private void createBucket() throws GlobalException {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param objectKey
     * @return
     * @throws GlobalException
     */
    public InputStream download(String objectKey) throws GlobalException {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
