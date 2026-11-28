package com.soft.common.enums;

/**
 * Redis key 前缀枚举
 * <p>统一管理所有业务前缀：中文含义用于缓存监控展示，敏感标记用于值脱敏。
 * 新增缓存前缀时在此追加枚举项，监控模块自动识别，无需另行登记。</p>
 *
 * @Author: cyx
 * @Description: redis key 前缀枚举（原 RedisConstant 常量类升级）
 * @DateTime: 2024/10/25 17:32
 **/
public enum RedisKeyPrefix {

    /**
     * 用户鉴权
     */
    AUTHORIZATION_USERNAME("radish:token:", "用户登录凭证（token）", true),

    /**
     * 邮箱验证码
     */
    EMAIL_CAPTCHA_KEY("radish:email:captcha:", "邮箱验证码", true),

    /**
     * 忘记密码验证码（与登录/注册验证码分开，避免同一验证码被跨流程使用）
     */
    FORGET_PASSWORD_CAPTCHA_KEY("radish:email:forget-password:", "忘记密码验证码", true),

    /**
     * 用户信息，tips：此处的"::"不能改为":"
     */
    USER_INFO("radish:users::", "用户信息缓存", true),

    /**
     * 登录图形验证码
     */
    LOGIN_GRAPHICS_CAPTCHA("radish:graphics:captcha:", "登录图形验证码", true),

    /**
     * 用户登录错误次数
     */
    USER_LOGIN_ERROR_TIME("radish:login-error-time:", "用户登录错误次数", true),

    /**
     * 公钥
     */
    RSA_PUBLIC_KEY("radish:rsa:public:", "RSA 公钥", true),

    /**
     * 私钥
     */
    RSA_PRIVATE_KEY("radish:rsa:private:", "RSA 私钥", true),

    /**
     * 限流
     */
    RATE_LIMIT_KEY("radish:rate:limit:", "接口限流", false),

    /**
     * 分片文件key
     */
    SLICE_FILE_KEY("radish:slice:keys:", "分片文件 key", false),

    /**
     * 分片文件索引key
     */
    SLICE_FILE_INDEX_KEY("radish:slice:index:", "分片文件索引", false),

    /**
     * 分片文件hash
     */
    SLICE_FILE_INFO("radish:slice:hash:", "分片文件 hash", false),

    /**
     * 锁key
     */
    LOCK_KEY("radish:lock:", "分布式锁", false),

    /**
     * 字典key-value缓存key，tips：此处的"::"不能改为":"
     */
    DICT_KEY("radish:dict::", "字典缓存", false),

    /**
     * 用户在线设备，Hash 结构：key = 前缀 + userId，field = deviceType，value = token
     */
    USER_DEVICES("radish:user-devices:", "用户在线设备", true),

    /**
     * 客户端指纹，按用户 + 设备类型存储：前缀 + userId + ":" + deviceType
     */
    FINGERPRINT("radish:fingerprint:", "客户端指纹", true),

    /**
     * 文件url签名
     */
    FILE_SIGNATURE_URL("radish:file-signature-url:", "文件 URL 签名", false),

    /**
     * 接口访问控制
     */
    ACCESS_CONTROL("radish:access-control:", "接口访问控制", false),

    /**
     * ping的状态，如果客户端回复，则删除该缓存；如果客户端没有回复，则在下一次心跳时，将其关闭
     */
    PING_STATUS("radish:ping-status:", "客户端心跳状态", false);

    private final String prefix;
    private final String description;
    private final boolean sensitive;

    RedisKeyPrefix(String prefix, String description, boolean sensitive) {
        this.prefix = prefix;
        this.description = description;
        this.sensitive = sensitive;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    /**
     * 前缀拼接完整 key
     */
    public String concat(String suffix) {
        return prefix + suffix;
    }

    /**
     * 根据完整 key 反查所属前缀枚举，未命中返回 null
     */
    public static RedisKeyPrefix matchOf(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        for (RedisKeyPrefix item : values()) {
            if (key.startsWith(item.prefix)) {
                return item;
            }
        }
        return null;
    }
}
