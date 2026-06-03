package com.soft.sys.core.handle;

import com.soft.sys.enums.ResultEnum;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 20:24
 **/

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseUtil.writeMsg(response, HttpStatus.FORBIDDEN.value(), R.fail(ResultEnum.PERMISSION_NOT_ENOUGH));
    }
}
