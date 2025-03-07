package com.soft.base.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: http请求工具类，建议不要使用，自己去按照工具类的写法去实现。
 * @DateTime: 2024/11/28 11:35
 **/

@Component
public class HttpUtil {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public HttpUtil(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * get请求，同步，响应参数为单个元素
     * @param url exp：www.spring.com
     * @param uri exp：/springboot/data/redis
     * @param params 请求参数
     * @param responseType 响应类型
     * @return
     * @param <T> 响应参数为单个元素。如：Map
     */
    public <T> T SyncMonoGet(String url, String uri, Map<String, Object> params, Map<String, String> header, Class<T> responseType) {
        WebClient webClient = webClientBuilder.baseUrl(url).build();
        return webClient
                .get()
                .uri(item -> buildUri(item, uri, params))
                .headers(item -> header.forEach(item::set))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * post请求，同步，响应参数为单个元素
     * @param url exp：www.spring.com
     * @param uri exp：/springboot/data/redis
     * @param params 请求参数
     * @param body 请求体
     * @param responseType 响应类型
     * @return
     * @param <T> 响应参数为单个元素。如：Map
     */
    public <T> T SyncMonoPost(String url, String uri, Map<String, Object> params, Map<String, String> header, Map<String, Object> body, Class<T> responseType) {
        WebClient webClient = webClientBuilder.baseUrl(url).build();
        return webClient
                .post()
                .uri(item -> buildUri(item, uri, params))
                .headers(item -> header.forEach(item::set))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * 构建uri
     * @param uriBuilder
     * @param uri
     * @param params
     * @return
     */
    private URI buildUri(UriBuilder uriBuilder, String uri, Map<String, Object> params) {
        uriBuilder.path(uri);
        params.forEach(uriBuilder::queryParam);
        return uriBuilder.build();
    }
}
