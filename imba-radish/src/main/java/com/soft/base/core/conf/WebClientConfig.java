package com.soft.base.core.conf;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @Author: cyx
 * @Description: http请求配置
 * @DateTime: 2024/11/4 14:01
 **/

@Configuration
public class WebClientConfig {

    @Value(value = "${web-client.response-timeout}")
    private Long responseTimeout;

    @Value(value = "${web-client.connect-timeout}")
    private Integer connectTimeout;

    @Bean
    public WebClient.Builder webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(responseTimeout))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
