package com.soft.base.core.handle;

import com.soft.base.enums.ResultEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.resultapi.R;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
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
        return new ResponseEntity<>(R.fail("请求数据解析失败"), HttpStatus.OK);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<R<Object>> noResourceFoundException(NoResourceFoundException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail("未找到资源"), HttpStatus.OK);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<R<Object>> globalException(GlobalException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail(ResultEnum.FAIL_NORMAL.getCode(), ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<R<Object>> accessDeniedException(AccessDeniedException ex) {
        log.error(ex.getLocalizedMessage());
        return new ResponseEntity<>(R.fail(ResultEnum.PERMISSION_NOT_ENOUGH.getCode(), ResultEnum.PERMISSION_NOT_ENOUGH.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<R<Object>> validationException(ValidationException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(R.fail(ResultEnum.FAIL_NORMAL.getCode(), ex.getLocalizedMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<R<Object>> accountStatusException(AccountStatusException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(R.fail(ResultEnum.FAIL_NORMAL.getCode(), ex.getLocalizedMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<R<Object>> badCredentialsException(BadCredentialsException ex) {
        log.error(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(R.fail(ResultEnum.FAIL_NORMAL.getCode(), ex.getLocalizedMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Object>> Exception(Exception ex) {
        log.error(ex.getLocalizedMessage(), ex);
        return new ResponseEntity<>(R.fail(), HttpStatus.OK);
    }
}
