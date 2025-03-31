package com.soft.base.handle;

import com.alibaba.fastjson2.JSONObject;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.HttpConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.TokenConstant;
import com.soft.base.model.dto.UserDto;
import com.soft.base.enums.ResultEnum;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.resultapi.R;
import com.soft.base.utils.ResponseUtil;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: cyx
 * @Description: TODO
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
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION).replaceFirst(TokenConstant.TOKEN_PREFIX, BaseConstant.BLANK_CHARACTER);
        String redisKeyAuth = RedisConstant.AUTHORIZATION_USERNAME + authorization;
        String username = (String) redisTemplate.opsForValue().get(redisKeyAuth);

        UserDto userDto = (UserDto) userDetailsService.loadUserByUsername(username);
        Long id = userDto.getId();

        Set<String> keys = new HashSet<>();
        keys.add(redisKeyAuth);
        keys.add(RedisConstant.USER_INFO + username);

        redisTemplate.delete(keys);

        @SuppressWarnings("unchecked")
        WebSocketConcreteHandler<String> webSocketConcreteHandler = (WebSocketConcreteHandler<String>) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.FORCE_OFFLINE.toString());
        JSONObject forceOfflineParam = new JSONObject();
        forceOfflineParam.put("order", WebSocketOrderEnum.FORCE_OFFLINE.toString());
        forceOfflineParam.put("receiver", id);
        TextMessage textMessage = new TextMessage(forceOfflineParam.toJSONString());
        webSocketConcreteHandler.handle(WebSocketSessionManager.getSession(id), textMessage);
        ResponseUtil.writeErrMsg(response, HttpConstant.SUCCESS, R.ok(ResultEnum.SUCCESS.getCode(), "注销成功"));
    }
}
