package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
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
    private String bucket;

    /**
     * 用户头像桶名
     */
    private String avatarBucket;

}
