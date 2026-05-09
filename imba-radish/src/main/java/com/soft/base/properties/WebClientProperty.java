package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2026/3/26
 * @description
 */

@Component
@Data
@ConfigurationProperties(prefix = "web-client")
public class WebClientProperty {

    /**
     * 响应超时；单位：秒
     */
    private Long responseTimeout;

    /**
     * 连接超时；单位：毫秒
     */
    private Integer connectTimeout;


    /**
     * 连接池
     */
    private Pool pool = new Pool();

    @Data
    public static class Pool {

        /**
         * 最大连接数
         */
        private Integer maxConnection;

        /**
         * 获取连接超时时间（秒）
         */
        private Long pendingAcquireTimeout;

        /**
         * 连接空闲最大时间（秒）
         */
        private Long maxIdleTime;

        /**
         * 连接最大存活时间（秒）
         */
        private Long maxLifeTime;

        /**
         * 后台清理间隔（秒）
         */
        private Long evictInBackground;
    }
}
