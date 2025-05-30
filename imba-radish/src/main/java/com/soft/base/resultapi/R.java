package com.soft.base.resultapi;

import com.soft.base.enums.ResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "restful响应结果集")
public class R<T> {

    @Schema(description = "时间戳")
    private Long timestamp;

    @Schema(description = "响应代码", example = "2001")
    private Integer code;

    @Schema(description = "响应文本", example = "成功")
    private String msg;

    @Schema(description = "响应结果集")
    private T data;

    public static <T> R<T> ok() {
        return setR(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(Integer code, String message) {
        return setR(code, message, null);
    }

    public static <T> R<T> ok(String message, T data) {
        return setR(ResultEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> ok(T data) {
        return setR(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> fail(String msg) {
        return setR(ResultEnum.FAIL_NORMAL.getCode(), msg, null);
    }

    public static <T> R<T> fail(Integer code, String msg) {
        return setR(code, msg, null);
    }

    public static <T> R<T> fail() {
        return setR(ResultEnum.FAIL_NORMAL.getCode(), ResultEnum.FAIL_NORMAL.getMessage(), null);
    }

    private static <T> R<T> setR(Integer code, String msg, T data) {
        R<T> result = new R<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

}
