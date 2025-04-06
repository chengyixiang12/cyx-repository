package com.soft.base.handle;

import com.soft.base.resultapi.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<R<Object>> methodNotSupportException(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail("不支持的请求方式"), HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail("非法日期格式"), HttpStatus.OK);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<R<Object>> noResourceFoundException(NoResourceFoundException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail("未找到资源"), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Object>> Exception(Exception ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail(), HttpStatus.OK);
    }
}
