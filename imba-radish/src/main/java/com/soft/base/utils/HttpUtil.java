package com.soft.base.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: http请求工具类
 * @DateTime: 2024/11/28 11:35
 **/

@Component
public class HttpUtil {

    private final WebClient.Builder webClientBuilder;

    private final LRUCache<String, WebClient> cache;

    @Autowired
    public HttpUtil(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.cache = CacheUtil.newLRUCache(50);
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
    public <T> T syncMonoGet(String url, String uri, Map<String, Object> params, Map<String, String> header, Class<T> responseType) {
        WebClient webClient = getWebClient(url);
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
    public <T> T syncMonoPost(String url, String uri, Map<String, Object> params, Map<String, String> header, Map<String, Object> body, Class<T> responseType) {
        WebClient webClient = getWebClient(url);
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
     * put请求，同步，响应参数为单个元素
     * @param url exp：www.spring.com
     * @param uri exp：/springboot/data/redis
     * @param params 请求参数
     * @param body 请求体
     * @param responseType 响应类型
     * @return
     * @param <T> 响应参数为单个元素。如：Map
     */
    public <T> T syncMonoPut(String url, String uri, Map<String, Object> params, Map<String, String> header, Map<String, Object> body, Class<T> responseType) {
        WebClient webClient = getWebClient(url);
        return webClient
                .put()
                .uri(item -> buildUri(item, uri, params))
                .headers(item -> header.forEach(item::set))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * delete请求，同步，响应参数为单个元素
     * @param url exp：www.spring.com
     * @param uri exp：/springboot/data/redis
     * @param params 请求参数
     * @param responseType 响应类型
     * @return
     * @param <T> 响应参数为单个元素。如：Map
     */
    public <T> T syncMonoDelete(String url, String uri, Map<String, Object> params, Map<String, String> header, Class<T> responseType) {
        WebClient webClient = getWebClient(url);
        return webClient
                .delete()
                .uri(item -> buildUri(item, uri, params))
                .headers(item -> header.forEach(item::set))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * get请求，异步，响应参数为多个元素
     * @param url exp：www.spring.com
     * @param uri exp：/springboot/data/redis
     * @param params 请求参数
     * @param responseType 响应类型
     * @return
     * @param <T> 响应参数为多个元素。如：List
     */
    public <T> Flux<T> asyncFluxGet(String url, String uri, Map<String, Object> params, Map<String, String> header, Class<T> responseType) {
        WebClient webClient = getWebClient(url);
        return webClient
                .get()
                .uri(item -> buildUri(item, uri, params))
                .headers(item -> header.forEach(item::set))
                .retrieve()
                .bodyToFlux(responseType);
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

    /**
     * 获取webClient实例
     * @param url
     * @return
     */
    private WebClient getWebClient(String url) {
        WebClient webClient = cache.get(url);
        if (webClient == null) {
            webClient = webClientBuilder.baseUrl(url).build();
            cache.put(url, webClient);
        }
        return webClient;
    }
}
