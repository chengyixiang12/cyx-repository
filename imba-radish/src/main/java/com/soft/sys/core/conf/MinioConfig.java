package com.soft.sys.core.conf;

import com.soft.sys.properties.MinioProperty;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;

/**
 * MinIO 对象存储客户端配置
 *
 * @author cyx
 * @date 2025-05-29
 */
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperty.getUrl())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .build();
    }
}
