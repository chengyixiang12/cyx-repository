package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/29 18:09
 **/
@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperty {

    /**
     * minio访问地址
     */
    private String url;

    /**
     * minio访问key
     */
    private String accessKey;

    /**
     * minio密钥
     */
    private String secretKey;

    /**
     * 通用桶名
     */
    private String defaultBucket;

    /**
     * 用户头像桶名
     */
    private String avatarBucket;

    /**
     * 文件url过期时间
     */
    private Integer expire;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

}
