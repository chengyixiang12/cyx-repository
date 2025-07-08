package com.soft.base.core.conf;

import com.soft.base.core.filter.AuthorizationVerifyFilter;
import com.soft.base.core.filter.RateLimitFilter;
import com.soft.base.core.handle.AuthenticationHandler;
import com.soft.base.core.handle.CustomAccessDeniedHandler;
import com.soft.base.core.handle.LogoutAfterSuccessHandler;
import com.soft.base.properties.AuthorizationIgnoreProperty;
import com.soft.base.properties.RateLimitProperty;
import com.soft.base.utils.UniversalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationHandler authenticationHandler;

    private final LogoutAfterSuccessHandler logoutAfterSuccessHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final UserDetailsService userDetailsService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UniversalUtil universalUtil;

    private final AuthorizationIgnoreProperty authorizationIgnoreProperty;

    private final RateLimitProperty rateLimitProperty;

    @Autowired
    public SecurityConfig(AuthenticationHandler authenticationHandler,
                          LogoutAfterSuccessHandler logoutAfterSuccessHandler,
                          UserDetailsService userDetailsService,
                          RedisTemplate<String, Object> redisTemplate,
                          UniversalUtil universalUtil,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          AuthorizationIgnoreProperty authorizationIgnoreProperty,
                          RateLimitProperty rateLimitProperty) {
        this.authenticationHandler = authenticationHandler;
        this.logoutAfterSuccessHandler = logoutAfterSuccessHandler;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
        this.universalUtil = universalUtil;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.authorizationIgnoreProperty = authorizationIgnoreProperty;
        this.rateLimitProperty = rateLimitProperty;
    }

    /**
     * 获取鉴权过滤器实例
     * @return
     */
    private AuthorizationVerifyFilter getAuthorizationVerifyFilter() {
        return new AuthorizationVerifyFilter(userDetailsService,redisTemplate);
    }

    /**
     * 获取限流过滤器实例
     * @return
     */
    private RateLimitFilter getRateLimitFilter() {
        return new RateLimitFilter(redisTemplate, rateLimitProperty);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用csrf
                .csrf(CsrfConfigurer::disable)
                // 禁用http basic认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 允许iframe嵌套
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 会话无状态
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 忽略不鉴权的路由
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(universalUtil.toArray(authorizationIgnoreProperty.getUrls(), String[].class)).permitAll()
                            .anyRequest().authenticated();
                })
                .logout(item -> item.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutAfterSuccessHandler))
                // 认证失败处理类
                .exceptionHandling(exc ->
                                exc
                                        // 用于处理未认证的请求（如未登录用户访问受保护的资源）
                                        .authenticationEntryPoint(authenticationHandler)
                                        // 用于处理已认证但没有权限访问资源的请求
                                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // 限流过滤器
                .addFilterBefore(getRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
                // 鉴权过滤器
                .addFilterBefore(getAuthorizationVerifyFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 强哈希算法 BCrypt 进行密码加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 使用 AuthenticationManagerBuilder 来配置 AuthenticationManager
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService) // 配置 UserDetailsService
                .passwordEncoder(passwordEncoder()); // 配置密码编码器

        return authenticationManagerBuilder.build(); // 返回 AuthenticationManager 实例
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/webjars/**", "/v3/**");
    }
}
