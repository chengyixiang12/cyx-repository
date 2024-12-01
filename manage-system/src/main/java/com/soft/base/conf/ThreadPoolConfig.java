package com.soft.base.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/7 20:07
 **/

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "cyxTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 设置核心线程数
        executor.setCorePoolSize(10);

        // 设置最大线程数
        executor.setMaxPoolSize(20);

        // 设置队列容量
        executor.setQueueCapacity(100);

        // 设置线程池名称前缀
        executor.setThreadNamePrefix("async-task-");

        // 设置最大空闲时间
        executor.setKeepAliveSeconds(60);

        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 是否允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);

        // 初始化线程池
        executor.initialize();

        return executor;
    }
}
