package com.soft.base.constants;

public class BaseConstant {

    /**
     * 状态
     */
    public static class Status {
        /**
         * 启用
         */
        public static final Integer STATUS_ENABLE = 1;

        /**
         * 禁用
         */
        public static final Integer STATUS_BAN = 0;
    }

    /**
     * 逻辑删除
     */
    public static class LogicDelete {
        /**
         * 存在
         */
        public static final Integer EXIST = 0;

        /**
         * 删除
         */
        public static final Integer DELETE = 1;
    }

    /**
     * minio
     */
    public static class Minio {
        /**
         * 默认存储位置；minio
         */
        public final static Integer MINIO = 1;

        /**
         * 存储位置；磁盘
         */
        public final static Integer DISK = 2;
    }

    public static class Permission {
        /**
         * 权限-启用
         */
        public final static Integer ENABLE = 1;
    }

    /**
     * 左下划线
     */
    public final static String LEFT_SLASH = "/";

    /**
     * aes偏移量
     */
    public final static String AES_OFFSET = "0000000001000001";

    /**
     * 默认管理员角色
     */
    public final static String MANAGER_ROLE_CODE = "ROLE_ADMIN";

    /**
     * 登录方式：密码
     */
    public final static String LOGIN_METHOD_PASSWORD = "password";

    /**
     * 登录方式：邮箱
     */
    public final static String LOGIN_METHOD_EMAIL = "email";

    /**
     * 登录方式：邮箱验证码长度
     */
    public final static Integer LOGIN_CAPTCHA_LENGTH = 6;

    public static class Role {
        /**
         * 固定角色标识
         */
        public final static Integer FIX_ROLE_FLAG = 1;

        /**
         * 非固定角色标识
         */
        public final static Integer UN_FIX_ROLE_FLAG = 0;

        /**
         * 默认角色标识
         */
        public final static Integer DEFAULT_ROLE_FLAG = 1;

        /**
         * 非默认角色标识
         */
        public final static Integer UN_DEFAULT_ROLE_FLAG = 0;
    }

    /**
     * 本地缓存永不过期
     */
    public final static Long LOCAL_CACHE_EXPIRE_NEVER = -1L;

    /**
     * tmp文件后缀
     */
    public final static String TMP_SUFFIX = ".tmp";

    /**
     * 缓冲池大小
     */
    public final static Integer BUFFER_SIZE = 1024;

    /**
     * 长整型初始值
     */
    public final static Long LONG_INIT_VAL = 0L;

    /**
     * 整型初始值
     */
    public final static Integer INTEGER_INIT_VAL = 0;

    /**
     * 文件流结束标识
     */
    public final static Long FILE_OVER_SIGN = -1L;

    /**
     * 最大登录错误次数：5次
     */
    public final static Long MAX_LOGIN_ERROR_TIME = 4L;

    /**
     * 图形验证码类型
     */
    public final static String GRAPHICS_CAPTCHA_TYPE = "PNG";

    /**
     * 部门层级加1
     */
    public final static Integer DEPT_LEVEL_ADD_ONE = 1;

    /**
     * 十六进制
     */
    public final static Integer SCALE_SIXTEEN = 16;

    /**
     * signum标识——正数
     */
    public final static Integer SIGN_NUM_POSITIVE = 1;

    /**
     * 哈希算法类型
     */
    public final static String TYPE_ALGORITHM = "SHA-256";

    /**
     * 菜单类型——目录
     */
    public final static String MENU_TYPE_DIRECTORY = "0";

    /**
     * 菜单类型——菜单
     */
    public final static String MENU_TYPE_MENU = "1";

    /**
     * 菜单类型——按钮
     */
    public final static String MENU_TYPE_BUTTON = "2";

    /**
     * 对话标识-用户
     */
    public static final Integer CHAT_TAG_USER = 1;

    /**
     * 对话标识-AI
     */
    public static final Integer CHAT_TAG_AI = 0;

    /**
     * 定时任务的默认分组
     */
    public static final String QUARTZ_DEFAULT_GROUP = "default";

    public static class QuartzType {
        /**
         * 定时任务-简单调度
         */
        public static final String QUARTZ_SIMPLE_SCHEDULE = "0";

        /**
         * 定时任务-cron调度
         */
        public static final String QUARTZ_CRON_SCHEDULE = "1";
    }
}
