package com.soft.sys.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.soft.common.constants.BaseConstant;
import com.soft.common.exception.GlobalException;
import com.soft.sys.properties.MinioProperty;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: minio工具类
 * @DateTime: 2024/10/26 15:23
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    private final MinioProperty minioProperty;

    /**
     * 分片大小
     */
    private static final long BURST_SIZE = 5L * 1024 * 1024;

    /**
     * 不分片
     */
    private static final Long BURST_FALSE = -1L;

    /**
     * 生成桶路径
     * @param fileKey 文件名
     * @param fileSuffix 文件后缀
     * @return
     */
    public String getObjectKey(String fileKey, String fileSuffix) {
        return DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN) + BaseConstant.LEFT_SLASH + fileKey + fileSuffix;
    }

    /**
     * 上传
     *
     * @param is       输入流
     * @param fileSize 文件大小 B
     * @throws GlobalException
     */
    public void upload(InputStream is, Long fileSize, String objectKey) {
        putObject(is, minioProperty.getDefaultBucket(), fileSize, objectKey);
    }

    /**
     * 调用minio的上传
     * @param is
     * @param bucket
     * @param fileSize
     * @param objectKey
     */
    private void putObject(InputStream is, String bucket, Long fileSize, String objectKey) {
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
            throw new GlobalException(e.getMessage());
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
            throw new GlobalException(e.getMessage());
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
            throw new GlobalException(e.getMessage());
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
            throw new GlobalException(e.getMessage());
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
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 判断对象是否存在
     * <p>
     * 下载前的预检：若对象已被删除（如用户在文件管理里删掉了头像文件），
     * 必须在设置响应头之前就发现，否则异常会发生在流式写出阶段，只能以断流/500 收场。
     *
     * @param objectKey 对象键
     * @return true-存在
     */
    public boolean exists(String objectKey) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperty.getDefaultBucket())
                    .object(objectKey)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            // 对象不存在时 MinIO 返回 NoSuchKey，属于预期情况，不记错误
            return false;
        } catch (Exception e) {
            log.warn("检查 MinIO 对象是否存在失败, objectKey={}", objectKey, e);
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param objectKey
     */
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperty.getDefaultBucket()).object(objectKey).build());
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
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
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
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
                    .expiry(minioProperty.getExpire(), TimeUnit.SECONDS)
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
                    .expiry(minioProperty.getExpire(), TimeUnit.SECONDS)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
