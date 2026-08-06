package com.soft.sys.core.handle;

import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.TokenConstant;
import com.soft.sys.enums.ResultEnum;
import com.soft.sys.model.dto.UserDTO;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.ResponseUtil;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/10/25 23:04
 **/

@Component
@Slf4j
public class LogoutAfterSuccessHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserDetailsService userDetailsService;

    public LogoutAfterSuccessHandler(RedisTemplate<String, Object> redisTemplate,
                                     UserDetailsService userDetailsService) {
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION).replaceFirst(TokenConstant.TOKEN_PREFIX, "");
        String redisKeyAuth = RedisConstant.AUTHORIZATION_USERNAME + authorization;
        String username = (String) redisTemplate.opsForValue().get(redisKeyAuth);

        UserDTO userDto = (UserDTO) userDetailsService.loadUserByUsername(username);
        Long id = userDto.getId();

        Set<String> keys = new HashSet<>();
        keys.add(redisKeyAuth);
        keys.add(RedisConstant.USER_INFO + username);
        keys.add(RedisConstant.FINGERPRINT + username);

        redisTemplate.delete(keys);

        // 移除用户websocket会话
        WebSocketSessionManager.removeSession(id);

        ResponseUtil.writeMsg(response, HttpStatus.OK.value(), R.ok(ResultEnum.SUCCESS.getCode(), "注销成功"));
    }
}
