package com.soft.base.constants;

/**
 * 正则常量
 */
public class RegexConstant {

    /**
     * 只包含英文字母和数字的正则
     */
    public final static String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";

    /**
     * 角色编码以ROLE_开头正则
     */
    public final static String ROLE_CODE_HEADER = "^ROLE_.*";

    /**
     * 邮箱正则
     */
    public final static String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
