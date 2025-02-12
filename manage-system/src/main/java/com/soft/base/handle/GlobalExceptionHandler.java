package com.soft.base.handle;

import com.soft.base.resultapi.R;
import com.soft.base.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

/**
 * 全局异常处理器
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleNoHandlerFoundException(HttpServletResponse response) throws IOException {
        ResponseUtil.writeErrMsg(response, HttpStatus.NOT_FOUND.value(), R.fail("不存在的请求路径"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void methodNotSupportException(HttpServletResponse response) throws IOException {
        ResponseUtil.writeErrMsg(response, HttpStatus.METHOD_NOT_ALLOWED.value(), R.fail("不支持的请求方式"));
    }
}
