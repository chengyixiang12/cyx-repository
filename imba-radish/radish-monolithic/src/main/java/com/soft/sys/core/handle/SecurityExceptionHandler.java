package com.soft.sys.core.handle;

import com.soft.common.enums.ResultEnum;
import com.soft.common.resultapi.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 认证 / 授权异常处理器
 *
 * <p>从 {@code com.soft.common.handle.GlobalExceptionHandler} 中拆出。原因是公共库中的
 * 全局异常处理器会被网关等未引入 spring-security 的模块扫描加载，若携带 Spring Security
 * 异常类型会导致启动时 NoClassDefFoundError。因此通用异常留在公共库，认证授权异常留在
 * 引入 spring-security 的服务内。
 *
 * @author cyx
 * @date 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * 权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<R<Object>> accessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail(ResultEnum.PERMISSION_NOT_ENOUGH));
    }

    /**
     * 账号被锁定
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<R<Object>> handleLockedException(LockedException e) {
        log.warn("账号被锁定: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("账号已被锁定，请联系管理员"));
    }

    /**
     * 账号被禁用
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<R<Object>> handleDisabledException(DisabledException e) {
        log.warn("账号被禁用: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("账号已被禁用"));
    }

    /**
     * 账号已过期
     */
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<R<Object>> handleAccountExpiredException(AccountExpiredException e) {
        log.warn("账号已过期: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("账号已过期，请联系管理员"));
    }

    /**
     * 凭证已过期
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<R<Object>> handleCredentialsExpiredException(CredentialsExpiredException e) {
        log.warn("凭证已过期: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("凭证已过期，请修改密码"));
    }

    /**
     * 用户名或密码错误
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<R<Object>> badCredentialsException(BadCredentialsException e) {
        log.warn("认证失败（用户名或密码错误）");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(R.fail("用户名或密码错误"));
    }
}
