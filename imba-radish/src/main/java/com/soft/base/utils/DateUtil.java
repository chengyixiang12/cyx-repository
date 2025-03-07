package com.soft.base.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: cyx
 * @Description: 日期工具类
 * @DateTime: 2024/10/26 15:34
 **/

@Component
public class DateUtil {

    private final static String EIGHT_BIT_STRIPING = "yyyy-MM-dd";

    private final static String EIGHT_BIT_NUMBER = "yyyyMMdd";

    private final static String EIGHT_BIT_SLASH = "yyyy/MM/dd";

    private final static String FOURTEEN_BIT_STRIPING = "yyyy-MM-dd HH:mm:ss";

    private final static String FOURTEEN_BIT_NUMBER = "yyyyMMddHHmmss";

    private final static String FOURTEEN_BIT_SLASH = "yyyy/MM/dd HH:mm:ss";

    /**
     * 生成14位横线日期
     * @return
     */
    public String date14Striping() {
        return new SimpleDateFormat(FOURTEEN_BIT_STRIPING).format(new Date());
    }

    /**
     * 生成14位数字日期
     * @return
     */
    public String date14Number() {
        return new SimpleDateFormat(FOURTEEN_BIT_NUMBER).format(new Date());
    }

    /**
     * 生成14位斜线日期
     * @return
     */
    public String date14Slash() {
        return new SimpleDateFormat(FOURTEEN_BIT_SLASH).format(new Date());
    }

    /**
     * 生成8位横线日期
     * @return
     */
    public String date8Striping() {
        return new SimpleDateFormat(EIGHT_BIT_STRIPING).format(new Date());
    }

    /**
     * 生成8位数字日期
     * @return
     */
    public String date8Number() {
        return new SimpleDateFormat(EIGHT_BIT_NUMBER).format(new Date());
    }

    /**
     * 生成8位斜线日期
     * @return
     */
    public String date8Slash() {
        return new SimpleDateFormat(EIGHT_BIT_SLASH).format(new Date());
    }
}
