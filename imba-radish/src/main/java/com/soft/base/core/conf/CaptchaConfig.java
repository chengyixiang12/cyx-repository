package com.soft.base.core.conf;

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
        // 基础配置
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        // 字符集配置
        properties.setProperty("kaptcha.textproducer.char.string",
                "23456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        // 字体配置
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        properties.setProperty("kaptcha.textproducer.font.size", "35");

        // 正确的颜色配置格式：RGB 数值
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 黑色

        // 背景配置
        properties.setProperty("kaptcha.background.clear.from", "255,255,255"); // 白色
        properties.setProperty("kaptcha.background.clear.to", "240,240,240");   // 浅灰色

        // 边框配置
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90"); // RGB 数值

        properties.setProperty("kaptcha.noise.impl",
                "com.google.code.kaptcha.impl.DefaultNoise");
        properties.setProperty("kaptcha.noise.color", "100,100,100"); // 单个灰色

        // 扭曲效果
        properties.setProperty("kaptcha.obscurificator.impl",
                "com.google.code.kaptcha.impl.WaterRipple");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
