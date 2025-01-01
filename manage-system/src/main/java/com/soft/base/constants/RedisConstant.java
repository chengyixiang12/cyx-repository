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
    public final static String TOKEN_BLACKLIST_KEY = "cyx:token:blacklist";

    /**
     * 用户鉴权
     */
    public final static String AUTHORIZATION_USERNAME = "cyx:token:";

    /**
     * 用户鉴权过期时间；单位：秒
     */
    public final static Long AUTHORIZATION_EXPIRE = 3600L;

    /**
     * 邮箱验证码
     */
    public final static String EMAIL_CAPTCHA_KEY = "cyx:email:captcha:";

    /**
     * 保存日志key
     */
    public final static String SYS_LOG_CACHE = "cyx:log:";

    /**
     * 用户信息
     */
    public final static String USER_INFO = "cyx::users::";

    /**
     * websocket中的用户会话
     */
    public final static String WS_USER_SESSION = "ws:user:session:";

    /**
     * websocket的用户会话保存过期时间；比心跳间隔长5秒；单位：秒
     */
    public final static Long WS_USER_SESSION_EXPIRE = 35L;

    /**
     * 通配符
     */
    public final static String WILDCARD_CHARACTER = "*";

    /**
     * 公钥
     */
    public final static String RSA_PUBLIC_KEY = "cyx:rsa:public:";

    /**
     * 私钥
     */
    public final static String RSA_PRIVATE_KEY = "cyx:rsa:private:";

    /**
     * 限流
     */
    public final static String RATE_LIMIT_KEY = "cyx:rate:limit:";

    /**
     * 分片文件key
     */
    public final static String SLICE_FILE_KEY = "cyx:slice:keys:";

    /**
     * 分片文件序号key
     */
    public final static String SLICE_FILE_NO_KEY = "cyx:slice:nos:";
}
