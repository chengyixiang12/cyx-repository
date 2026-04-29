package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cyx
 * @description: 系统配置
 * @date 2026-04-29
 */
@Component
@ConfigurationProperties(prefix = "radish")
@Data
public class RadishProperty {

    private Captcha captcha = new Captcha();

    private Graphics graphics = new Graphics();

    private Token token = new Token();

    private Lock lock = new Lock();

    @Data
    public static class Captcha {

        /**
         * 主题
         */
        private String topic;

        /**
         * 验证码过期时间（秒）
         */
        private Integer expireTime;
    }

    @Data
    public static class Graphics {

        /**
         * 图形验证码过期时间（秒）
         */
        private Integer expireTime;
    }

    @Data
    public static class Token {

        /**
         * token过期时间（秒）
         */
        private Integer expireTime;
    }

    @Data
    public static class Lock {

        /**
         * 锁过期时间（秒）
         */
        private Integer expire;

        /**
         * 最大重试次数
         */
        private Integer maxRetry;

        /**
         * 重试间隔时间（毫秒）
         */
        private Integer retryIntervalTime;
    }
}
