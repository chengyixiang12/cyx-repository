package com.soft.base.core.conf;

import com.soft.base.properties.WebClientProperty;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * @Author: cyx
 * @Description: http请求配置
 * @DateTime: 2024/11/4 14:01
 **/

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WebClientProperty webClientProperty;

    @Bean
    public WebClient.Builder webClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("radish-http-pool")
                .maxConnections(webClientProperty.getPool().getMaxConnection())
                .pendingAcquireTimeout(Duration.ofSeconds(webClientProperty.getPool().getPendingAcquireTimeout()))
                .maxIdleTime(Duration.ofSeconds(webClientProperty.getPool().getMaxIdleTime()))
                .maxLifeTime(Duration.ofSeconds(webClientProperty.getPool().getMaxLifeTime()))
                .evictInBackground(Duration.ofSeconds(webClientProperty.getPool().getEvictInBackground()))
                .build();
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(webClientProperty.getResponseTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperty.getConnectTimeout());
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
