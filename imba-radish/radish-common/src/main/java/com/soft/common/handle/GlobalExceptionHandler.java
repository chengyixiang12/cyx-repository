package com.soft.common.handle;

import com.soft.common.enums.ResultEnum;
import com.soft.common.exception.GlobalException;
import com.soft.common.resultapi.R;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器（通用部分）
 * 统一处理各类异常，返回规范的 HTTP 状态码和响应格式
 *
 * <p>注意：本类刻意不引用任何 Spring Security 异常类型。因为它位于公共库中，
 * 会被网关等未引入 spring-security 的模块扫描并加载，一旦携带 Security 异常
 * 就会导致启动时 NoClassDefFoundError。
 * 认证/授权相关的异常处理见 {@code com.soft.sys.core.handle.SecurityExceptionHandler}。
 *
 * @author cyx
 * @description:
 * @date 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<R<Object>> methodNotSupportException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方式: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("不支持的请求方式，支持的方式：" + e.getSupportedHttpMethods()));
    }

    /**
     * 请求数据解析失败
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<R<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求数据解析失败: {}", e.getMessage());

        String message = "请求数据格式错误";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("Required request body is missing")) {
                message = "请求体不能为空";
            } else if (e.getMessage().contains("JSON parse error")) {
                message = "JSON格式错误，请检查请求数据";
            }
        }

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail(message));
    }

    /**
     * 资源不存在（404）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<R<Object>> noResourceFoundException(NoResourceFoundException e) {
        log.warn("资源未找到: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("未找到资源"));
    }

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<R<Object>> globalException(GlobalException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail());
    }

    /**
     * 参数校验失败
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<R<Object>> validationException(ValidationException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail(e.getMessage()));
    }

    /**
     * 方法参数校验失败（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResponseEntity.badRequest().body(R.fail(message));
    }

    /**
     * 方法参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<R<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数 '%s' 类型错误，期望类型: %s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型不匹配: {}", message);
        return ResponseEntity.badRequest().body(R.fail(message));
    }

    /**
     * 其他未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Object>> exception(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail());
    }
}
