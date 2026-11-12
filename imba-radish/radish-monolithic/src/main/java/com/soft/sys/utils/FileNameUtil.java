package com.soft.sys.utils;

import com.soft.common.exception.GlobalException;

/**
 * 上传文件名统一处理。
 * 去掉路径部分、控制字符，并限制长度；避免文件名被当作磁盘路径或写进响应头时出问题。
 */
public final class FileNameUtil {

    private static final int MAX_LENGTH = 255;

    private FileNameUtil() {
    }

    public static String sanitize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new GlobalException("文件名不能为空");
        }
        String name = raw.replace('\\', '/');
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        StringBuilder sb = new StringBuilder();
        for (char c : name.trim().toCharArray()) {
            if (!Character.isISOControl(c)) {
                sb.append(c);
            }
        }
        String result = sb.toString().trim();
        if (result.isEmpty() || ".".equals(result) || "..".equals(result)) {
            throw new GlobalException("文件名不合法");
        }
        if (result.length() > MAX_LENGTH) {
            String suffix = suffixOf(result);
            result = result.substring(0, MAX_LENGTH - suffix.length()) + suffix;
        }
        return result;
    }

    /** 取扩展名；无扩展名时返回空串（原实现 substring(-1) 会直接抛异常） */
    public static String suffixOf(String name) {
        if (name == null) {
            return "";
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "";
        }
        return name.substring(dot);
    }
}
