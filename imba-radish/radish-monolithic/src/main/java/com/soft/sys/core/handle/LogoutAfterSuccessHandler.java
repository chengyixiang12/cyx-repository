package com.soft.sys.core.handle;

import com.soft.common.constants.RedisConstant;
import com.soft.common.constants.TokenConstant;
import com.soft.common.enums.ResultEnum;
import com.soft.common.exception.GlobalException;
import com.soft.sys.model.dto.UserDTO;
import com.soft.common.resultapi.R;
import com.soft.common.utils.ResponseUtil;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
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
    public void onLogoutSuccess(HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION).replaceFirst(TokenConstant.TOKEN_PREFIX, "");
        String redisKeyAuth = RedisConstant.AUTHORIZATION_USERNAME + authorization;
        String username = (String) redisTemplate.opsForValue().get(redisKeyAuth);

        if (StringUtils.isBlank(username)) {
            throw new GlobalException("凭证已过期");
        }
        UserDTO userDto = (UserDTO) userDetailsService.loadUserByUsername(username);
        Long id = userDto.getId();
        String devicesKey = RedisConstant.USER_DEVICES + id;

        // 反查当前 token 对应的设备类型，只清理当前设备
        String deviceType = null;
        Map<Object, Object> devices = redisTemplate.opsForHash().entries(devicesKey);
        for (Map.Entry<Object, Object> entry : devices.entrySet()) {
            if (authorization.equals(String.valueOf(entry.getValue()))) {
                deviceType = String.valueOf(entry.getKey());
                break;
            }
        }

        Set<String> keys = new HashSet<>();
        keys.add(redisKeyAuth);
        keys.add(RedisConstant.USER_INFO + username);
        if (deviceType != null) {
            keys.add(RedisConstant.FINGERPRINT + id + ":" + deviceType);
            redisTemplate.opsForHash().delete(devicesKey, deviceType);
            WebSocketSessionManager.removeSession(id, deviceType);
        } else {
            // 兼容旧数据：没有设备记录时，关闭该用户所有会话
            redisTemplate.delete(devicesKey);
            WebSocketSessionManager.removeAll(id);
        }

        redisTemplate.delete(keys);

        ResponseUtil.writeMsg(response, HttpStatus.OK.value(), R.ok(ResultEnum.SUCCESS.getCode(), "注销成功"));
    }
}
