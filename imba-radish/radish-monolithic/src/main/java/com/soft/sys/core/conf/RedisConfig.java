package com.soft.sys.core.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.data.redis.health.DataRedisHealthIndicator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/10/25 17:27
 **/
@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 Jackson 3 的 GenericJacksonJsonRedisSerializer 进行 JSON 序列化/反序列化
        GenericJacksonJsonRedisSerializer serializer = createJsonSerializer();

        // 设置序列化方式
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        // 设置 key 的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        return template;
    }

    /**
     * 显式声明命令式 Redis 健康检查。
     * <p>Spring Boot 4 的 {@code DataRedisReactiveHealthContributorAutoConfiguration}（reactive 侧）
     * 与 {@code DataRedisHealthContributorAutoConfiguration}（命令式侧）的 bean 方法同名
     * {@code redisHealthContributor}，且命令式被声明为 after reactive：reactive 侧先注册后，
     * 命令式侧因 {@code @ConditionalOnMissingBean(names = "redisHealthContributor")} 让位；
     * 而 {@code ReactiveHealthIndicator} 并不是 {@code HealthContributor}，
     * 进不了 {@code HealthContributorRegistry}——表现为 {@code registry.getContributor("redis")}
     * 返回 null，监控页 Redis 永远显示「未启用」。
     * 此处手动注册命令式检查，两个自动配置都会因此让位。</p>
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisHealthIndicator")
    public DataRedisHealthIndicator redisHealthIndicator(RedisConnectionFactory connectionFactory) {
        return new DataRedisHealthIndicator(connectionFactory);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // UserDetails专用配置（使用JDK序列化）
        RedisCacheConfiguration userDetailsCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30));  // 设置30分钟过期

        // 默认配置（使用JSON序列化）
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(createJsonSerializer()));

        return RedisCacheManager.builder(factory)
                .withCacheConfiguration("radish:users", userDetailsCacheConfig) // 特定缓存配置
                .cacheDefaults(defaultConfig) // 默认配置
                .transactionAware() // 增加事务
                .build();
    }

    /**
     * Spring Data Redis 4.0 起 {@code GenericJackson2JsonRedisSerializer} 已废弃，
     * 改用 Jackson 3 的 {@link GenericJacksonJsonRedisSerializer}。
     *
     * <p>开启 default typing 以写入 {@code @class} 类型信息，保证
     * {@code RedisTemplate<String, Object>} 能反序列化回原始类型；该行为与旧序列化器一致。</p>
     */
    private GenericJacksonJsonRedisSerializer createJsonSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .enableSpringCacheNullValueSupport()
                .build();
    }
}
