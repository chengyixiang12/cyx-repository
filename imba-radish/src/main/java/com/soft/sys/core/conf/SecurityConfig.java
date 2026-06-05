package com.soft.sys.core.conf;

import com.soft.sys.core.filter.AuthorizationVerifyFilter;
import com.soft.sys.core.filter.RateLimitFilter;
import com.soft.sys.core.handle.AuthenticationHandler;
import com.soft.sys.core.handle.CustomAccessDeniedHandler;
import com.soft.sys.core.handle.LogoutAfterSuccessHandler;
import com.soft.sys.properties.AuthorizationIgnoreProperty;
import com.soft.sys.properties.RateLimitProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

/**
 * Spring Security 核心配置
 *
 * <p>过滤器链顺序：RateLimitFilter → AuthorizationVerifyFilter → 业务处理</p>
 * <p>认证方式：无状态 JWT（从 Redis 校验 token → 用户名映射）</p>
 *
 * @author cyx
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationHandler authenticationHandler;
    private final LogoutAfterSuccessHandler logoutAfterSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthorizationIgnoreProperty authorizationIgnoreProperty;
    private final RateLimitProperty rateLimitProperty;

    /**
     * BCrypt 强哈希密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager，由 {@link AuthenticationConfiguration} 自动装配
     * {@link UserDetailsService} 和 {@link PasswordEncoder}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离：禁用表单登录、HTTP Basic、CSRF
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                // 无状态会话 — JWT 鉴权，服务端不维护 HttpSession
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 安全上下文存入 Request 属性（匹配无状态模式）
                .securityContext(context -> context
                        .securityContextRepository(new RequestAttributeSecurityContextRepository()))

                // 响应头
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)  // 允许 iframe
                )

                // 权限规则：白名单放行，其余全部认证
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authorizationIgnoreProperty.getUrls().toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                )

                // 异常处理
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(authenticationHandler)          // 未认证
                        .accessDeniedHandler(customAccessDeniedHandler)           // 无权限
                )

                // 登出
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutAfterSuccessHandler)
                )

                // 过滤器链（顺序敏感）
                .addFilterBefore(
                        new AuthorizationVerifyFilter(userDetailsService, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new RateLimitFilter(redisTemplate, rateLimitProperty),
                        AuthorizationVerifyFilter.class);

        return http.build();
    }
}
