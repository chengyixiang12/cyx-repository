package com.soft.sys.core.filter;

import com.soft.sys.constants.HttpConstant;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.TokenConstant;
import com.soft.sys.enums.ResultEnum;
import com.soft.sys.model.dto.UserDto;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 鉴权验证过滤器
 */
@Slf4j
public class AuthorizationVerifyFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final RedisTemplate<String, Object> redisTemplate;

    public AuthorizationVerifyFilter(UserDetailsService userDetailsService,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (StringUtils.isBlank(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (!token.startsWith(TokenConstant.TOKEN_PREFIX)) {
                log.info("非法鉴权：{}", token);
                ResponseUtil.writeMsg(response, HttpConstant.SUCCESS, R.fail(ResultEnum.NOT_AUTHENTICATION.getCode(), ResultEnum.NOT_AUTHENTICATION.getMessage()));
                return;
            }
            // 去除token前缀
            token = token.substring(TokenConstant.TOKEN_PREFIX_LENGTH);
            String username = (String) redisTemplate.opsForValue().get(RedisConstant.AUTHORIZATION_USERNAME + token);
            if (StringUtils.isEmpty(username)) {
                log.info("{}，鉴权过期", token);
                ResponseUtil.writeMsg(response, HttpConstant.SUCCESS, R.fail(ResultEnum.AUTHENTICATION_FAIL.getCode(), ResultEnum.AUTHENTICATION_FAIL.getMessage()));
                return;
            }
            UserDto user = (UserDto) this.userDetailsService.loadUserByUsername(username);
            // 在 Spring Security 中设置用户身份
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseUtil.writeMsg(response,  HttpConstant.SERVER_ERROR, R.fail(ResultEnum.FAIL_NORMAL.getCode(), ResultEnum.FAIL_NORMAL.getMessage()));
        } finally {
            // 清除安全上下文
            SecurityContextHolder.clearContext();
        }
    }
}
