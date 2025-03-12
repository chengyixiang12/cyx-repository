package com.soft.base.utils;

import com.soft.base.exception.GlobalException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

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

    private static final String REGEX = "^\\d+$";

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

    /**
     * 解析字符串格式日期
     * @param dateStr
     * @return
     * @throws GlobalException
     */
    public LocalDateTime parse(String dateStr) throws GlobalException {
        String tmp;
        String[] dateArr;
        String[] dateTimeArr = dateStr.trim().split(" ");
        int year = 1;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;
        int second = 0;
        switch (dateTimeArr.length) {
            case 2: {
                // 时分秒
                String date = dateTimeArr[1];
                dateArr = date.split(":");
                switch (dateArr.length) {
                    case 3: {
                        // 秒
                        tmp = dateArr[2];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        second = Integer.parseInt(tmp);
                        if (0 > second || 60 < second) {
                            throw new GlobalException("非法时间格式");
                        }
                    }
                    case 2: {
                        // 分
                        tmp = dateArr[1];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        minute = Integer.parseInt(tmp);
                        if (0 > minute || 60 < minute) {
                            throw new GlobalException("非法时间格式");
                        }
                    }
                    case 1: {
                        // 时
                        tmp = dateArr[0];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        hour = Integer.parseInt(tmp);
                        if (0 > hour || 12 < hour) {
                            throw new GlobalException("非法时间格式");
                        }
                        break;
                    }
                    default: {
                        throw new GlobalException("非法时间格式");
                    }
                }
            }
            case 1: {
                // 年月日
                String date = dateTimeArr[0];
                dateArr = date.split("-");
                switch (dateArr.length) {
                    case 3: {
                        // 日
                        tmp = dateArr[2];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        day = Integer.parseInt(tmp);
                        if (0 >= day || 31 < day) {
                            throw new GlobalException("非法时间格式");
                        };
                    }
                    case 2: {
                        // 月
                        tmp = dateArr[1];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        month = Integer.parseInt(tmp);
                        if (0 >= month || 12 < month) {
                            throw new GlobalException("非法时间格式");
                        }
                    }
                    case 1: {
                        // 年
                        tmp = dateArr[0];
                        if (!Pattern.matches(REGEX, tmp)) {
                            throw new GlobalException("非法时间格式");
                        }
                        year = Integer.parseInt(tmp);
                        break;
                    }
                    default: {
                        throw new GlobalException("非法时间格式");
                    }
                }
                break;
            }
            default: {
                throw new GlobalException("非法时间格式");
            }
        }
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }
}
