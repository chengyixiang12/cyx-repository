package com.soft.base.core.conf;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author: cyx
 * @Description: 异步配置类
 * @DateTime: 2024/11/30 22:08
 **/

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public AsyncConfig(@Qualifier(value = "radishTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @NotNull
    @Override
    public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor;
    }
}
