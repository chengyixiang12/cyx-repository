package com.soft.sys.core.conf;

import com.soft.sys.properties.WebClientProperty;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
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
    public WebClient.Builder webClient() throws SSLException {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("radish-web-client-pool")
                .maxConnections(webClientProperty.getPool().getMaxConnection())
                .pendingAcquireTimeout(Duration.ofSeconds(webClientProperty.getPool().getPendingAcquireTimeout()))
                .maxIdleTime(Duration.ofSeconds(webClientProperty.getPool().getMaxIdleTime()))
                .maxLifeTime(Duration.ofSeconds(webClientProperty.getPool().getMaxLifeTime()))
                .evictInBackground(Duration.ofSeconds(webClientProperty.getPool().getEvictInBackground()))
                .build();
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // 仅测试用
                .build();
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(webClientProperty.getResponseTimeout()))
                .secure(spec -> spec.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperty.getConnectTimeout());
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
