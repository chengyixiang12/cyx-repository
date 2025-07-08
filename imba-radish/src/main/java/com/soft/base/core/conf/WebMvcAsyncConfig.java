package com.soft.base.core.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/28 16:34
 **/
@Configuration
public class WebMvcAsyncConfig implements WebMvcConfigurer {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public WebMvcAsyncConfig(@Qualifier("cyxTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(threadPoolTaskExecutor);
        configurer.setDefaultTimeout(30_000);
    }
}
