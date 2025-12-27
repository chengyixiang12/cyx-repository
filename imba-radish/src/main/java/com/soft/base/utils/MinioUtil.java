package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import com.soft.base.exception.GlobalException;
import com.soft.base.properties.MinioProperty;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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

    private final MinioProperty minioProperty;

    /**
     * 分片大小
     */
    private static final long BURST_SIZE = 5L * 1024 * 1024;

    /**
     * 不分片
     */
    private static final Long BURST_FALSE = -1L;

    @Autowired
    public MinioUtil(MinioClient minioClient, DateUtil dateUtil, MinioProperty minioProperty) {
        this.minioClient = minioClient;
        this.dateUtil = dateUtil;
        this.minioProperty = minioProperty;
    }

    /**
     * 生成桶路径
     * @param fileKey 文件名
     * @param fileSuffix 文件后缀
     * @return
     */
    public String getObjectKey(String fileKey, String fileSuffix) {
        return dateUtil.date8Number() + BaseConstant.LEFT_SLASH + fileKey + fileSuffix;
    }

    /**
     * 上传
     * @param is 输入流
     * @param fileSize 文件大小 B
     * @return
     * @throws GlobalException
     */
    public String upload(InputStream is, Long fileSize, String objectKey) {
        putObject(is, minioProperty.getDefaultBucket(), fileSize, objectKey);
        return objectKey;
    }

    /**
     * 上传
     * @param is 输入流
     * @param bucket 桶名
     * @param fileSize 文件大小 B
     * @return
     * @throws GlobalException
     */
    public String upload(InputStream is, String bucket, Long fileSize, String objectKey) {
        putObject(is, bucket, fileSize, objectKey);
        return objectKey;
    }

    /**
     * 调用minio的上传
     * @param is
     * @param bucket
     * @param fileSize
     * @param objectKey
     */
    public void putObject(InputStream is, String bucket, Long fileSize, String objectKey) {
        try {
            if (!existBucket(bucket)) {
                createBucket(bucket);
            }
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(is, fileSize, fileSize < BURST_SIZE ? BURST_FALSE : BURST_SIZE)
                    .build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 判断桶是否存在
     * @return
     * @throws GlobalException
     */
    private Boolean existBucket() {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperty.getDefaultBucket()).build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 判断桶是否存在
     * @return
     * @throws GlobalException
     */
    private Boolean existBucket(String bucket) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 创建桶
     * @throws GlobalException
     */
    private void createBucket(String bucket) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 下载文件
     * @param objectKey
     * @return
     * @throws GlobalException
     */
    public InputStream download(String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(minioProperty.getDefaultBucket()).object(objectKey).build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 下载文件
     * @param objectKey
     * @param bucket
     * @return
     * @throws GlobalException
     */
    public InputStream download(String bucket, String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (Exception e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 删除文件
     * @param objectKey
     */
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperty.getDefaultBucket()).object(objectKey).build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件
     * @param bucket
     * @param objectKey
     */
    public void delete(String bucket, String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成文件url
     * @param bucket
     * @param objectKey
     * @param headerMap
     * @return
     */
    public String generateUrl(String bucket, String objectKey, Map<String, String> headerMap) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .method(Method.GET)
                    .extraQueryParams(headerMap)
                    .expiry(minioProperty.getExpire(), minioProperty.getTimeUnit())
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 生成文件url
     * @param bucket
     * @param objectKey
     * @return
     */
    public String generateUrl(String bucket, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .method(Method.GET)
                    .expiry(minioProperty.getExpire(), minioProperty.getTimeUnit())

                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
