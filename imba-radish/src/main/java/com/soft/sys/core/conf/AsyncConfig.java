package com.soft.sys.core.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: cyx
 * @Description: 异步配置类
 * @DateTime: 2024/11/30 22:08
 **/

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 异步指定线程池
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        AsyncTaskExecutor adaptedExecutor = new TaskExecutorAdapter(virtualThreadExecutor);
        return new DelegatingSecurityContextAsyncTaskExecutor(adaptedExecutor); // 让异步线程具备上下文传递的能力
    }
}
