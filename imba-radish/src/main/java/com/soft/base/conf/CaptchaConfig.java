package com.soft.base.conf;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: cyx
 * @Description: 图形验证码配置
 * @DateTime: 2025/2/11 11:31
 **/

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 设置验证码图片的宽度
        properties.setProperty("kaptcha.image.width", "150");
        // 设置验证码图片的高度
        properties.setProperty("kaptcha.image.height", "50");
        // 设置验证码字符集
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 设置验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 设置验证码字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        // 设置验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 设置验证码背景颜色
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");
        // 设置验证码干扰线
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 设置验证码边框
        properties.setProperty("kaptcha.border", "no");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
