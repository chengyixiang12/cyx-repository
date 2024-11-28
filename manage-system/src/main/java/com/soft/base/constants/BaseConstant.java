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
     * 逻辑删除——删除
     */
    public final static String DEL_FLAG_UNEXIST = "0";

    /**
     * 是否启用——启用
     */
    public final static Integer ENABLED_TRUE = 1;

    /**
     * 是否启用——禁用
     */
    public final static Integer ENABLED_FALSE = 0;

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
     * 点
     */
    public final static String ENG_POINT = ".";

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
     * 字典类型；禁用
     */
    public final static String DICT_TYPE_STATUS_FORBIDDEN = "0";

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
     * 默认角色标识
     */
    public final static Integer DEFAULT_ROLE_FLAG = 1;

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
}
