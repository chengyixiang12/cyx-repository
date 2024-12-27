package com.soft.base.conf;

import com.soft.base.aspect.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: cyx
 * @Description: mvc配置
 * @DateTime: 2024/11/28 17:23
 **/

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    public WebConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(rateLimitInterceptor)
                .order(1).addPathPatterns("/**")
                .excludePathPatterns("/webjars/**", "/v3/**");
    }
}
