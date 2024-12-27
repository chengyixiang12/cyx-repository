package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.entity.SysUser;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.enums.LogTypeEnum;
import com.soft.base.exception.CaptChaErrorException;
import com.soft.base.request.LoginRequest;
import com.soft.base.request.RegisterRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.AuthService;
import com.soft.base.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@Tag(name = "鉴权")
@RequestMapping(value = "/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public AuthController(AuthService authService,
                          RedisTemplate<String, Object> redisTemplate) {
        this.authService = authService;
        this.redisTemplate = redisTemplate;
    }

    @SysLog(value = "用户登录", module = LogModuleEnum.AUTHORIZATION, type = LogTypeEnum.LOGIN, name = "#request.username")
    @PostMapping("/login")
    @Operation(summary = "登录")
    public R<LoginVo> authenticate(@RequestBody LoginRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            return R.fail("用户名不能为空");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            return R.fail("密码不能为空");
        }
        if (StringUtils.isBlank(request.getLoginMethod())) {
            return R.fail("登录方式不能为空");
        }
        try {
            LoginVo loginVo = authService.authenticate(request);
            return R.ok(loginVo);
        } catch (BadCredentialsException
                 | DisabledException
                 | LockedException
                 | CredentialsExpiredException
                 | AccountExpiredException
                 | CaptChaErrorException e) {
            log.error(e.getMessage(), e);
            return R.fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PostMapping(value = "/register")
    @Operation(summary = "注册")
    public R register(@RequestBody RegisterRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            return R.fail("用户名不能为空");
        }
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名不能使用中文");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            return R.fail("密码不能为空");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            return R.fail("邮箱不能为空");
        }
        if (!Pattern.matches(RegexConstant.EMAIL, request.getEmail())) {
            return R.fail("非法邮箱");
        }
        if (StringUtils.isBlank(request.getCaptcha())) {
            return R.fail("验证码不能为空");
        }

        try {
            String captchaCache = (String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + request.getEmail());
            if (!request.getCaptcha().equals(captchaCache)) {
                return R.fail("验证码错误，请检查您的邮箱是否更改或者验证码是否过期");
            }
            SysUser sysUser = new SysUser();
            sysUser.setUsername(request.getUsername());
            sysUser.setPassword(request.getPassword());
            sysUser.setNickname(request.getNickname());
            sysUser.setEmail(request.getEmail());
            authService.register(sysUser);
            redisTemplate.delete(RedisConstant.EMAIL_CAPTCHA_KEY + request.getEmail());
            return R.ok("注册成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
