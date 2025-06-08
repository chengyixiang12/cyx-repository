package com.soft.base.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.soft.base.annotation.SysLock;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.entity.SysUser;
import com.soft.base.exception.InvalidLoginMethodException;
import com.soft.base.model.request.LoginRequest;
import com.soft.base.model.request.RegisterRequest;
import com.soft.base.model.vo.LoginVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.AuthService;
import com.soft.base.service.SysUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@Tag(name = "鉴权")
@RequestMapping(value = "/auth")
@Slf4j
@Validated
public class AuthController {

    @Value(value = "${radish.graphics.expire-time}")
    private Long graphicsCaptchaExpireTime;

    private final AuthService authService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final DefaultKaptcha captchaProducer;

    private final SysUsersService sysUsersService;

    @Autowired
    public AuthController(AuthService authService,
                          RedisTemplate<String, Object> redisTemplate,
                          DefaultKaptcha captchaProducer,
                          SysUsersService sysUsersService) {
        this.authService = authService;
        this.redisTemplate = redisTemplate;
        this.captchaProducer = captchaProducer;
        this.sysUsersService = sysUsersService;
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public R<LoginVo> authenticate(@RequestBody @Valid LoginRequest request) {
        if (StringUtils.isBlank(request.getLoginMethod())) {
            return R.fail("登录方式不能为空");
        }
        if (BaseConstant.LOGIN_METHOD_PASSWORD.equals(request.getLoginMethod())) {
            if (StringUtils.isBlank(request.getGraphicsCaptcha())) {
                return R.fail("图形验证码不能为空");
            }
            if (StringUtils.isBlank(request.getUsername())) {
                return R.fail("用户名不能为空");
            }
            if (StringUtils.isBlank(request.getPassword())) {
                return R.fail("密码不能为空");
            }
            if (StringUtils.isBlank(request.getUuid())) {
                return R.fail("唯一标识不能为空");
            }
        } else if (BaseConstant.LOGIN_METHOD_EMAIL.equals(request.getLoginMethod())) {
            if (StringUtils.isBlank(request.getEmail())) {
                return R.fail("邮箱不能为空");
            }
            if (StringUtils.isBlank(request.getEmailCaptcha())) {
                return R.fail("邮箱验证码不能为空");
            }
        } else {
            return R.fail("未知的登录方式");
        }
        if (BaseConstant.LOGIN_METHOD_PASSWORD.equals(request.getLoginMethod())) {
            String graphicsCaptcha = (String) redisTemplate.opsForValue().get(RedisConstant.LOGIN_GRAPHICS_CAPTCHA + request.getUuid());
            if (StringUtils.isBlank(graphicsCaptcha)) {
                return R.fail("图形验证码过期");
            }
            if (!graphicsCaptcha.equalsIgnoreCase(request.getGraphicsCaptcha())) {
                return R.fail("图形验证码错误");
            }
        } else if (BaseConstant.LOGIN_METHOD_EMAIL.equals(request.getLoginMethod())) {
            SysUser sysUser = sysUsersService.getUserByEmail(request.getEmail());
            Boolean existEmailCaptcha = redisTemplate.hasKey(RedisConstant.EMAIL_CAPTCHA_KEY + sysUser.getUsername());
            if (!existEmailCaptcha) {
                return R.fail("邮箱验证码已过期");
            }
        }
        LoginVo loginVo = authService.authenticate(request);
        return R.ok(loginVo);
    }

    @SysLock(name = "user")
    @PostMapping(value = "/register")
    @Operation(summary = "注册")
    public R<Object> register(@RequestBody @Valid RegisterRequest request) {
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名只能包含英文字母或数字");
        }
        if (sysUsersService.existsUsername(request.getUsername())) {
            return R.fail("用户名已注册");
        }
        if (!Pattern.matches(RegexConstant.EMAIL, request.getEmail())) {
            return R.fail("非法邮箱");
        }
        if (sysUsersService.existsEmail(request.getEmail())) {
            return R.fail("邮箱已注册");
        }

        String captchaCache = (String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + request.getEmail());
        if (!request.getVerificationCode().equals(captchaCache)) {
            return R.fail("验证码错误，请检查您的邮箱是否更改或者验证码是否过期");
        }
        SysUser sysUser = new SysUser();
        sysUser.setUsername(request.getUsername());
        sysUser.setPassword(request.getPassword());
        sysUser.setNickname(request.getNickname());
        sysUser.setEmail(request.getEmail());
        authService.register(sysUser);
        redisTemplate.delete(RedisConstant.EMAIL_CAPTCHA_KEY + request.getEmail());
        return R.ok();
    }

    @GetMapping(value = "/getGraphicCaptcha")
    @Operation(summary = "获取图形验证码")
    @Parameter(name = "uuid", description = "唯一标识", required = true, in = ParameterIn.PATH)
    public ResponseEntity<Object> getGraphicCaptcha(@RequestParam(value = "uuid", required = false) @NotNull(message = "唯一标识不能为空") String uuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String text = captchaProducer.createText();

        redisTemplate.opsForValue().set(RedisConstant.LOGIN_GRAPHICS_CAPTCHA + uuid, text, graphicsCaptchaExpireTime, TimeUnit.SECONDS);

        BufferedImage image = captchaProducer.createImage(text);
        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        ImageIO.write(image, BaseConstant.GRAPHICS_CAPTCHA_TYPE, bis);
        byte[] byteArray = bis.toByteArray();

        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(byteArray);
    }
}
