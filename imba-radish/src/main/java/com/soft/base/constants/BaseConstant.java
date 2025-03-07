package com.soft.base.constants;

public class BaseConstant {

    /**
     * 默认启用
     */
    public final static Integer DEF_STATUS = 1;

    /**
     * 逻辑删除——存在
     */
    public final static String DEL_FLAG_EXIST = "1";

    /**
     * 邮箱文本路径
     */
    public final static String EMAIL_CONTENT_PATH = "template/emailcontent.txt";

    /**
     * 左下划线
     */
    public final static String LEFT_SLASH = "/";

    /**
     * 请求头信息
     */
    public final static String HEADER_USER_AGENT = "User-Agent";

    /**
     * 全通配符
     */
    public final static String ALL_WILDCARD_CHARACTER = "**";

    /**
     * 转义符
     */
    public final static String ESCAPE_CHARACTER = "\\";

    /**
     * 英文冒号
     */
    public final static String ENG_COLON = ":";

    /**
     * 英文破折号
     */
    public final static String ENG_DASH = "-";

    /**
     * 空白符
     */
    public final static String BLANK_CHARACTER = "";

    /**
     * 分片大小
     */
    public final static Long BURST_SIZE = 5L * 1024 * 1024;

    /**
     * 不分片
     */
    public final static Long BURST_FALSE = -1L;

    /**
     * 文件后缀的点
     */
    public final static String FILE_POINT_SUFFIX = ".";

    /**
     * 默认存储位置；minio
     */
    public final static String DEFAULT_STORAGE_LOCATION = "1";

    /**
     * 存储位置；磁盘
     */
    public final static String DISK_STORAGE_LOCATION = "2";

    /**
     * aes偏移量
     */
    public final static String AES_OFFSET = "0000000001000001";

    /**
     * 默认管理员角色
     */
    public final static String MANAGER_ROLE_CODE = "ROLE_ADMIN";

    /**
     * 字典类型；启用
     */
    public final static String DICT_TYPE_STATUS_ENABLE = "1";

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

    /**
     * 左方括弧
     */
    public final static String LEFT_SQUARE_BRACKET = "[";

    /**
     * 右方括弧
     */
    public final static String RIGHT_SQUARE_BRACKET = "]";

    /**
     * 权限-启用
     */
    public final static String PERMISSION_ENABLE = "1";

    /**
     * 限流窗口扩大1000倍
     */
    public final static Long WINDOW_SIZE_EXPAND_MULTIPLE = 1000L;

    /**
     * 本地缓存过期时间转换秒
     */
    public final static Long LOCAL_CACHE_EXPIRE_SECOND = 1000L;

    /**
     * 本地缓存过期时间转换天
     */
    public final static Long LOCAL_CACHE_EXPIRE_DAY = 24 * 60 * 60 * 1000L;

    /**
     * 本地缓存过期时间转换时
     */
    public final static Long LOCAL_CACHE_EXPIRE_HOURS = 60 * 60 * 1000L;

    /**
     * 本地缓存过期时间转换分
     */
    public final static Long LOCAL_CACHE_EXPIRE_MINUTES = 60 * 1000L;

    /**
     * 本地缓存永不过期
     */
    public final static Long LOCAL_CACHE_EXPIRE_NEVER = -1L;

    /**
     * 导出部门excel名称
     */
    public final static String EXPORT_DEPT_EXCEL_NAME = "部门.xlsx";

    /**
     * excel文件后缀
     */
    public final static String EXCEL_SUFFIX = ".xlsx";

    /**
     * txt文件后缀
     */
    public final static String TXT_SUFFIX = ".txt";

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
}
