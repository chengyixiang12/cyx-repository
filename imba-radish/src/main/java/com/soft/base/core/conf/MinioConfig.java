package com.soft.base.core.conf;

import com.soft.base.properties.MinioProperty;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return MinioClient
                .builder()
                .endpoint(minioProperty.getUrl())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .httpClient(builder.callTimeout(Duration.ofMillis(minioProperty.getConnectTimeout())).build())
                .build();
    }
}
