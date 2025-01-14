package com.soft.base.handle;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.HttpConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.TokenConstant;
import com.soft.base.enums.ResultEnum;
import com.soft.base.resultapi.R;
import com.soft.base.utils.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/25 23:04
 **/

@Component
@Slf4j
public class LogoutAfterSuccessHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    public LogoutAfterSuccessHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION).replaceFirst(TokenConstant.TOKEN_PREFIX, BaseConstant.BLANK_CHARACTER);
        String username = (String) redisTemplate.opsForValue().get(RedisConstant.AUTHORIZATION_USERNAME + authorization);
        redisTemplate.delete(RedisConstant.USER_INFO + username);
        log.info("{} already remove in redis", username);
        redisTemplate.delete(RedisConstant.AUTHORIZATION_USERNAME + authorization);
        log.info("token \"{}\" already remove in redis", authorization);
        ResponseUtil.writeErrMsg(response, HttpConstant.SUCCESS, R.ok(ResultEnum.SUCCESS.getCode(), "注销成功"));
    }
}
