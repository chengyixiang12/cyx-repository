package com.soft.sys.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 异步任务配置
 * <p>
 * 使用虚拟线程（Virtual Threads, JEP 444）作为 {@code @Async} 的默认执行器。
 * 虚拟线程由 JVM 统一调度，每个任务创建一个轻量级虚拟线程（~KB 级开销），
 * 无需像平台线程那样池化复用，适合高并发、短生命周期的异步任务场景。
 * <p>
 * 通过 {@link DelegatingSecurityContextAsyncTaskExecutor} 包装，
 * 确保 Spring Security 上下文（认证信息、SecurityContext）在异步线程间自动传递，
 * 避免在子线程中丢失用户身份信息。
 *
 * @author cyx
 * @since 2024/11/30
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 注册虚拟线程执行器 Bean。
     * <p>
     * Bean 名称为 {@code taskExecutor}，Spring Boot 自动配置会将其
     * 识别为 {@code @Async} 的默认执行器。通过 Spring 的 CGLIB 代理，
     * 容器内多次调用此方法始终返回同一个单例实例。
     *
     * @return 支持安全上下文传递的虚拟线程执行器
     */
    @Bean
    public Executor taskExecutor() {
        AsyncTaskExecutor executor = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    /**
     * 显式指定 {@code @Async} 注解使用的默认线程池。
     * <p>
     * 委托给 {@link #taskExecutor()} Bean，由 Spring 容器代理保证返回同一实例，
     * 确保全应用异步任务统一使用虚拟线程执行。
     *
     * @return 默认异步执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }
}
