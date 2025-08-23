package com.soft.base.core.handle;

import com.soft.base.constants.HttpConstant;
import com.soft.base.enums.ResultEnum;
import com.soft.base.resultapi.R;
import com.soft.base.utils.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 */
@Component
public class AuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ResponseUtil.writeMsg(response, HttpConstant.UNAUTHORIZED, R.fail(ResultEnum.NOT_AUTHENTICATION.getCode(), ResultEnum.NOT_AUTHENTICATION.getMessage()));
    }
}
