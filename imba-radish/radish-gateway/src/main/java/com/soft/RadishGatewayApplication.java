package com.soft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API 网关启动类。
 *
 * <p>⚠️ 必须使用 {@code scanBasePackages} 收窄扫描范围，不能用默认的 {@code com.soft} 全包扫描。
 * 原因：{@code radish-common} 中的 {@code HttpUtil} 是 {@code @Component} 且构造器注入
 * {@code WebClient.Builder}，而网关默认不引入 spring-webflux，全包扫描会导致启动时
 * {@code NoClassDefFoundError: WebClient}。
 *
 * <p>{@code com.soft.common.handle} 需显式包含，以便加载统一的全局异常处理器。
 *
 * @author cyx
 */
@SpringBootApplication(scanBasePackages = {"com.soft.gateway", "com.soft.common.handle"})
public class RadishGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadishGatewayApplication.class, args);
    }
}
