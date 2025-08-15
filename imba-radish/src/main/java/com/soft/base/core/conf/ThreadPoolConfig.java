package com.soft.base.core.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: cyx
 * @Description: 线程池配置
 * @DateTime: 2024/11/7 20:07
 **/

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean(name = "applicationTaskExecutor")
    public ThreadPoolTaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 设置核心线程数
        executor.setCorePoolSize(cpuCores);

        // 设置最大线程数
        executor.setMaxPoolSize(cpuCores * 2);

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

    @Bean(name = "radishTaskExecutor")
    public ThreadPoolTaskExecutor radishTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 设置核心线程数
        executor.setCorePoolSize(cpuCores);

        // 设置最大线程数
        executor.setMaxPoolSize(cpuCores);

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
