package com.soft.nacos;

/**
 * Nacos 接入相关的公共常量与服务名约定。
 *
 * <p>各微服务通过依赖 {@code radish-nacos} 获得 Nacos 能力，服务名统一在此登记，
 * 避免网关路由、服务间调用等处散落字符串字面量。
 *
 * @author cyx
 */
public final class NacosConstants {

    private NacosConstants() {
    }

    /** Nacos 服务端默认地址（可用环境变量 NACOS_SERVER_ADDR 覆盖） */
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1:8848";

    /** 默认分组 */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /** 默认命名空间 */
    public static final String DEFAULT_NAMESPACE = "public";

    // ==================== 服务名 ====================

    /** 网关 */
    public static final String SERVICE_GATEWAY = "radish-gateway";

    /** 系统设置服务 */
    public static final String SERVICE_SYSTEM = "radish-system";

    /** 业务服务 */
    public static final String SERVICE_BUSINESS = "radish-business";

}
