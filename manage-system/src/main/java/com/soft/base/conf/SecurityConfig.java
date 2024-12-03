package com.soft.base.conf;

import com.soft.base.filter.JwtRequestFilter;
import com.soft.base.handle.AuthenticationHandler;
import com.soft.base.handle.CustomAccessDeniedHandler;
import com.soft.base.handle.LogoutAfterSuccessHandler;
import com.soft.base.properties.JwtIgnoreProperty;
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

    private final JwtIgnoreProperty jwtIgnoreProperty;

    @Autowired
    public SecurityConfig(AuthenticationHandler authenticationHandler,
                          LogoutAfterSuccessHandler logoutAfterSuccessHandler,
                          UserDetailsService userDetailsService,
                          RedisTemplate<String, Object> redisTemplate,
                          UniversalUtil universalUtil,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          JwtIgnoreProperty jwtIgnoreProperty) {
        this.authenticationHandler = authenticationHandler;
        this.logoutAfterSuccessHandler = logoutAfterSuccessHandler;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
        this.universalUtil = universalUtil;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtIgnoreProperty = jwtIgnoreProperty;
    }

    private JwtRequestFilter getJwtRequestFilter() {
        return new JwtRequestFilter(userDetailsService,redisTemplate);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用csrf
                .csrf(CsrfConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(universalUtil.toArray(jwtIgnoreProperty.getUrls(), String[].class)).permitAll()
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
                .addFilterBefore(getJwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
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
