package com.soft.base.constants;

/**
 * @Author: cyx
 * @Description: redis常量类
 * @DateTime: 2024/10/25 17:32
 **/
public class RedisConstant {

    /**
     * 黑名单
     */
    @Deprecated
    public static final String TOKEN_BLACKLIST_KEY = "radish:token:blacklist";

    /**
     * 用户鉴权
     */
    public static final String AUTHORIZATION_USERNAME = "radish:token:";

    /**
     * 邮箱验证码
     */
    public static final String EMAIL_CAPTCHA_KEY = "radish:email:captcha:";

    /**
     * 保存日志key
     */
    public static final String SYS_LOG_CACHE = "radish:log:";

    /**
     * 用户信息，tips：此处的"::"不能改为":"
     */
    public static final String USER_INFO = "radish:users::";

    /**
     * 登录图形验证码
     */
    public static final String LOGIN_GRAPHICS_CAPTCHA = "radish:graphics:captcha:";

    /**
     * 用户登录错误次数
     */
    public static final String USER_LOGIN_ERROR_TIME = "radish:login-error-time:";

    /**
     * websocket中的用户会话
     */
    public static final String WS_USER_SESSION = "ws:user:session:";

    /**
     * websocket的用户会话保存过期时间；比心跳间隔长5秒；单位：秒
     */
    public static final Long WS_USER_SESSION_EXPIRE = 35L;

    /**
     * 通配符
     */
    public static final String WILDCARD_CHARACTER = "*";

    /**
     * 公钥
     */
    public static final String RSA_PUBLIC_KEY = "radish:rsa:public:";

    /**
     * 私钥
     */
    public static final String RSA_PRIVATE_KEY = "radish:rsa:private:";

    /**
     * 限流
     */
    public static final String RATE_LIMIT_KEY = "radish:rate:limit:";

    /**
     * 分片文件key
     */
    public static final String SLICE_FILE_KEY = "radish:slice:keys:";

    /**
     * 分片文件索引key
     */
    public static final String SLICE_FILE_INDEX_KEY = "radish:slice:index:";

    /**
     * 分片文件hash
     */
    public static final String SLICE_FILE_INFO = "radish:slice:hash:";

    /**
     * 锁key
     */
    public static final String LOCK_KEY = "radish:lock:";

    /**
     * 字典key-value缓存key，tips：此处的"::"不能改为":"
     */
    public static final String DICT_KEY = "radish:dict::";

    /**
     * 客户端指纹
     */
    public static final String FINGERPRINT = "radish:fingerprint:";

    /**
     * 文件url签名
     */
    public static final String FILE_SIGNATURE_URL = "radish:file-signature-url:";

    /**
     * webClient实例
     */
    public static final String HTTP_CLIENT_BUILDER = "radish:http-client-builder:";
}
