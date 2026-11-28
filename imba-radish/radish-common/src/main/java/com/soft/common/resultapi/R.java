package com.soft.common.resultapi;

import com.soft.common.enums.ResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "restful响应结果集")
@Getter
public class R<T> {

    @Schema(description = "时间戳")
    private final Long timestamp;

    @Schema(description = "响应代码", example = "2001")
    private final Integer code;

    @Schema(description = "响应文本", example = "成功")
    private final String msg;

    @Schema(description = "响应结果集")
    private final T data;

    @Schema(description = "额外的参数")
    private Map<String, Object> extra;

    private R(Long timestamp, Integer code, String msg, T data) {
        this.timestamp = timestamp;
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> R<T> ok() {
        return setR(ResultEnum.SUCCESS.getCode(), null, null);
    }

    public static <T> R<T> ok(String message) {
        return setR(ResultEnum.SUCCESS.getCode(), message, null);
    }

    public static <T> R<T> ok(String message, T data) {
        return setR(ResultEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> ok(T data) {
        return setR(ResultEnum.SUCCESS.getCode(), null, data);
    }

    public static <T> R<T> tip(Integer code, String message) {
        return setR(code, message, null);
    }

    public static <T> R<T> fail(String msg) {
        return setR(ResultEnum.FAIL_NORMAL.getCode(), msg, null);
    }

    public static <T> R<T> fail(Integer code, String msg) {
        return setR(code, msg, null);
    }

    public static <T> R<T> fail(ResultEnum resultEnum) {
        return setR(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    public static <T> R<T> fail() {
        return setR(ResultEnum.FAIL_NORMAL.getCode(), ResultEnum.FAIL_NORMAL.getMessage(), null);
    }

    public R<T> setExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new LinkedHashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    public R<T> setExtra(Map<String, Object> extra) {
        if (this.extra == null) {
            this.extra = new LinkedHashMap<>();
        }
        this.extra.putAll(extra);
        return this;
    }

    private static <T> R<T> setR(Integer code, String msg, T data) {
        return new R<>(System.currentTimeMillis(), code, msg, data);
    }

}
