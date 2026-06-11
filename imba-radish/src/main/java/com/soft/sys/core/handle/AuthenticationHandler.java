package com.soft.sys.core.handle;

import com.soft.sys.enums.ResultEnum;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 认证失败处理器
 */
@Component
public class AuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ResponseUtil.writeMsg(response, HttpStatus.UNAUTHORIZED.value(), R.fail(ResultEnum.NOT_AUTHENTICATION));
    }
}
