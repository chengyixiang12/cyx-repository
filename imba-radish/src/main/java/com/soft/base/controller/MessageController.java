package com.soft.base.controller;

import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.core.annotation.AccessControl;
import com.soft.base.entity.SysUser;
import com.soft.base.rabbitmq.producer.EmailProduce;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysUsersService;
import com.soft.base.utils.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/15 18:04
 **/

@RestController
@RequestMapping(value = "/message")
@Slf4j
@Validated
@Tag(name = "消息")
public class MessageController {

    private final EmailProduce emailProduce;

    private final RedisTemplate<String, Object> redisTemplate;

    private final SysUsersService sysUsersService;

    @Value(value = "${radish.captcha.expire-time}")
    private Long expireTime;

    public MessageController(EmailProduce emailProduce,
                             RedisTemplate<String, Object> redisTemplate,
                             SysUsersService sysUsersService) {
        this.emailProduce = emailProduce;
        this.redisTemplate = redisTemplate;
        this.sysUsersService = sysUsersService;
    }

    @AccessControl(times = 3, interval = 10, unit = TimeUnit.MINUTES)
    @GetMapping(value = "/sendRegistCaptcha")
    @Operation(summary = "发送注册验证码")
    @Parameter(name = "email", description = "邮箱地址", required = true, in = ParameterIn.QUERY)
    public R<Object> sendRegistCaptcha(@RequestParam(value = "email", required = false) @NotBlank(message = "邮箱不能为空") String email) {
        if (!Pattern.matches(RegexConstant.EMAIL, email)) {
            return R.fail("非法邮箱");
        }
        if (StringUtils.isNotBlank((String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + email))) {
            return R.fail("请勿重复发送验证码");
        }
        emailProduce.send(email, getCaptchaEmailContent(String.valueOf(Math.abs(email.hashCode()))));
        return R.ok("验证码已发送，请留意您的邮箱", null);
    }

    @AccessControl(times = 2, interval = 10, unit = TimeUnit.MINUTES)
    @GetMapping(value = "/sendLoginCaptcha")
    @Operation(summary = "发送登录验证码")
    @Parameter(name = "email", description = "邮箱", required = true, in = ParameterIn.QUERY)
    public R<Object> sendLoginCaptcha(@RequestParam(value = "email", required = false) @NotBlank(message = "邮箱不能为空") String email) {
        SysUser sysUser = sysUsersService.getUserByEmail(email);
        if (sysUser == null) {
            return R.fail("邮箱尚未注册，请前往注册");
        }

        String username = sysUser.getUsername();

        if (StringUtils.isNotBlank((String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + username))) {
            return R.fail("请勿重复发送验证码");
        }
        emailProduce.send(email, getCaptchaEmailContent(String.valueOf(Math.abs(email.hashCode()))));
        return R.ok("验证码已发送，请留意您的邮箱", null);
    }

    @GetMapping(value = "/getMessage")
    @Operation(summary = "获取消息")
    private R<Object> getMessage() {
        return R.ok();
    }

    /**
     * 获取验证码邮箱内容
     * @return
     */
    private String getCaptchaEmailContent(String uniqueTag) {
        String captChat = CommonUtil.generate(BaseConstant.LOGIN_CAPTCHA_LENGTH);
        redisTemplate.opsForValue().set(RedisConstant.EMAIL_CAPTCHA_KEY + uniqueTag, captChat, expireTime, TimeUnit.SECONDS);
        return  "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>验证码邮件</title><style>body {background-color: #f9f9f9;margin: 0;padding: 0;font-family: Arial, sans-serif;color: #333;}.email-container {max-width: 600px;margin: 50px auto;background-color: #ffffff;padding: 20px;border-radius: 8px;box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);text-align: center;}.email-header {font-size: 18px;font-weight: bold;margin-bottom: 20px;}.email-content p {font-size: 16px;line-height: 1.6;margin: 10px 0;} .email-content strong{color: #d9534f; }.email-footer{font-size: 12px;color: #999;margin-top: 20px;}</style></head><body><div class=\"email-container\"><div class=\"email-header\">验证码邮件</div><div class=\"email-content\"><p>尊敬的用户您好！</p><p>您的验证码是：<strong>" +
                captChat +
                "</strong>，请您在 5 分钟内完成验证。</p><p>如果该验证码不是您本人申请的，请忽略此邮件。</p><p>感谢您的使用！</p></div><div class=\"email-footer\">此邮件由系统自动发送，请勿回复。</div></div></body></html>";
    }
}
