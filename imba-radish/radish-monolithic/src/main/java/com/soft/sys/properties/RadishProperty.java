package com.soft.sys.properties;

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

    private Log log = new Log();

    private Workflow workflow = new Workflow();

    private Upload upload = new Upload();

    @Data
    public static class Workflow {

        /**
         * 工作流通知邮件主题
         */
        private String topic;
    }

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

    @Data
    public static class Upload {

        /**
         * 单文件上限（字节）2GB，必须与前端 MAX_FILE_SIZE、nginx client_max_body_size 对齐
         */
        private Long maxTotalFileSize;

        /**
         * 未完成分片保留时长（小时）：留一段时间用于断点续传，超时由 ChunkCleanupJob 回收
         */
        private Integer chunkTtlHours;

        /**
         * 清理任务执行间隔（毫秒）
         */
        private Integer cleanupIntervalMs;

        /**
         * 启动后的首次延迟（毫秒）
         */
        private Integer cleanupInitialDelayMs;

        /**
         * 是否顺带清理改造前遗留的 tmp/{md5} 孤儿分片目录
         */
        private Boolean cleanupLegacy;
    }

    @Data
    public static class Log {

        /**
         * 是否开启日志记录
         */
        private Boolean enable;

        /**
         * 日志位置
         */
        private String location;
    }
}
