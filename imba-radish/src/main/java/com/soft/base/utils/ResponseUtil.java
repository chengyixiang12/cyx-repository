package com.soft.base.utils;

import cn.hutool.json.JSONUtil;
import com.soft.base.resultapi.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseUtil {

    /**
     * 消息返回
     *
     * @param response  HttpServletResponse
     * @param httpCode 状态码
     */
    public static void writeMsg(HttpServletResponse response, int httpCode, R<Object> result) {

        response.setStatus(httpCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSONUtil.toJsonStr(result));
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }

}
