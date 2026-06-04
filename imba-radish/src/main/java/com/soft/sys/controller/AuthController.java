package com.soft.sys.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.constants.RegexConstant;
import com.soft.sys.core.annotation.SysLock;
import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.LoginRequest;
import com.soft.sys.model.request.RegisterRequest;
import com.soft.sys.model.vo.LoginVo;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.resultapi.R;
import com.soft.sys.service.AuthService;
import com.soft.sys.service.SysUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@Tag(name = "鉴权")
@RequestMapping(value = "/auth")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final RadishProperty radishProperty;

    private final AuthService authService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final SysUsersService sysUsersService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public R<LoginVo> authenticate(@RequestBody @Valid LoginRequest request) {
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
            Boolean existEmailCaptcha = redisTemplate.hasKey(RedisConstant.EMAIL_CAPTCHA_KEY + request.getEmail());
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
    @Parameter(name = "uuid", description = "唯一标识", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<Object> getGraphicCaptcha(@RequestParam(value = "uuid", required = false) @NotNull(message = "唯一标识不能为空") String uuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Pragma", "No-cache");
        headers.set("Cache-Control", "no-cache");

        // 创建验证码对象（宽150，高50，验证码位数4，干扰线数量20）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(150, 50, 4, 20);
        String code = captcha.getCode();

        redisTemplate.opsForValue().set(RedisConstant.LOGIN_GRAPHICS_CAPTCHA + uuid, code, radishProperty.getGraphics().getExpireTime(), TimeUnit.SECONDS);

        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        captcha.write(bis);
        byte[] byteArray = bis.toByteArray();

        headers.setContentType(MediaType.IMAGE_PNG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(byteArray);
    }
}
