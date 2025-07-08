package com.soft.base.core.conf;

import com.soft.base.properties.MinioProperty;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final MinioProperty minioProperty;

    @Autowired
    public MinioConfig(MinioProperty minioProperty) { this.minioProperty = minioProperty;}

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(minioProperty.getUrl())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .build();
    }
}
