package com.soft.base.controller;

import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.exception.RepeatSendCaptChaException;
import com.soft.base.rabbitmq.producer.CaptchaProduce;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/15 18:04
 **/

@RestController
@RequestMapping(value = "/message")
@Slf4j
@Tag(name = "消息队列")
public class MessageController {

    private final CaptchaProduce captchaProduce;

    private final RedisTemplate<String, Object> redisTemplate;

    private final SysUsersService sysUsersService;

    @Autowired
    public MessageController(CaptchaProduce captchaProduce,
                             RedisTemplate<String, Object> redisTemplate,
                             SysUsersService sysUsersService) {
        this.captchaProduce = captchaProduce;
        this.redisTemplate = redisTemplate;
        this.sysUsersService = sysUsersService;
    }

    @GetMapping(value = "/sendRegistCaptcha")
    @Operation(summary = "发送注册验证码")
    @Parameter(name = "email", description = "邮箱地址", required = true, in = ParameterIn.QUERY)
    public R sendRegistCaptcha(@RequestParam(value = "email", required = false) String email) {
        if (StringUtils.isBlank(email)) {
            return R.fail("邮箱不能为空");
        }
        if (!Pattern.matches(RegexConstant.EMAIL, email)) {
            return R.fail("非法邮箱");
        }
        try {
            if (StringUtils.isNotBlank((String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + email))) {
                throw new RepeatSendCaptChaException("请勿重复发送验证码");
            }
            captchaProduce.sendRegistCaptcha(email);
            return R.ok("验证码已发送，请留意您的邮箱");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/sendLoginCaptcha")
    @Operation(summary = "发送登录验证码")
    @Parameter(name = "username", description = "用户名", required = true, in = ParameterIn.QUERY)
    public R sendLoginCaptcha(@RequestParam(value = "username", required = false) String username) {
        if (StringUtils.isBlank(username)) {
            return R.fail("用户名不能为空");
        }
        try {
            if (StringUtils.isNotBlank((String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + username))) {
                throw new RepeatSendCaptChaException("请勿重复发送验证码");
            }
            boolean flag = sysUsersService.checkUsernameExist(username);
            if (!flag) {
                return R.fail("用户不存在");
            }
            captchaProduce.sendLoginCaptcha(username);
            return R.ok("验证码已发送，请留意您的邮箱");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
