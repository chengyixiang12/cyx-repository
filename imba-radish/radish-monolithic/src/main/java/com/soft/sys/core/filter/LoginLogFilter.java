package com.soft.sys.core.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.soft.common.enums.LogModuleEnum;
import com.soft.common.enums.LogTypeEnum;
import com.soft.common.enums.ResultEnum;
import com.soft.common.utils.CommonUtil;
import com.soft.sys.enums.LogLevelEnum;
import com.soft.sys.model.dto.LogDTO;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.event.SysLogPublisher;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * 登录日志过滤器
 *
 * <p>只拦截 {@code POST /auth/login}，成功登录后自动记录一条 {@link LogTypeEnum#LOGIN} 类型的日志，
 * 业务代码无需任何改动。</p>
 *
 * <p>之所以用 Filter 而不是 HandlerInterceptor：需要读取 {@code @RequestBody} 和响应体，
 * 而 HandlerInterceptor 无法替换 request/response，拿不到已被 MVC 消费的 body。</p>
 *
 * <p>注意：该 Filter 不加 {@code @Component}，由 {@code SecurityConfig} 手动注册，
 * 避免 Spring Boot 将其自动注册到 Servlet 容器导致重复执行。</p>
 *
 * @author cyx
 */
@Slf4j
public class LoginLogFilter extends OncePerRequestFilter {

    private static final String LOGIN_URI = "/auth/login";

    /**
     * 请求体缓存上限，登录请求体很小
     */
    private static final int REQUEST_CACHE_SIZE = 16 * 1024;

    /**
     * 请求参数中需要脱敏的字段
     */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "graphicsCaptcha", "emailCaptcha", "verificationCode", "fingerprint");

    private final SysLogPublisher sysLogProduce;

    private final RadishProperty radishProperty;

    public LoginLogFilter(SysLogPublisher sysLogProduce, RadishProperty radishProperty) {
        this.sysLogProduce = sysLogProduce;
        this.radishProperty = radishProperty;
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !LOGIN_URI.equals(pathWithinApplication(request));
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)
                ? uri.substring(contextPath.length()) : uri;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (!Boolean.TRUE.equals(radishProperty.getLog().getEnable())) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, REQUEST_CACHE_SIZE);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            try {
                recordLoginLog(requestWrapper, responseWrapper, System.currentTimeMillis() - start);
            } catch (Exception e) {
                // 日志记录失败不能影响登录结果
                log.warn("记录登录日志失败: {}", e.getMessage());
            } finally {
                // 必须回写，否则前端收不到响应
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void recordLoginLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                                long executionTime) {
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        JSONObject responseJson = parseObject(responseBody);
        if (responseJson == null) {
            return;
        }

        Integer code = responseJson.getInteger("code");
        // 仅记录成功登录
        if (!Objects.equals(ResultEnum.SUCCESS.getCode(), code)) {
            return;
        }

        LogDTO logDto = new LogDTO();
        logDto.setType(LogTypeEnum.LOGIN.getCode());
        logDto.setModuleName(LogModuleEnum.AUTHORIZATION.getName());
        logDto.setOperationDesc("用户登录");
        logDto.setLogLevel(LogLevelEnum.INFO.getCode());
        logDto.setStatusCode(code);
        logDto.setExecutionTime(executionTime);
        logDto.setRequestUrl(request.getRequestURI());
        logDto.setRequestMethod(request.getMethod());
        logDto.setIpAddress(CommonUtil.getIp(request));
        logDto.setRequestParams(maskRequestParams(request.getContentAsByteArray()));
        logDto.setResponseResult(buildResponseSummary(responseJson));
        setBrowserInfo(logDto, request.getHeader("User-Agent"));
        // 登录成功后由 LoginVO 返回用户主键，日志列表可据此关联 sys_user 显示昵称
        JSONObject data = responseJson.getJSONObject("data");
        if (data != null) {
            Long userId = data.getLong("userId");
            logDto.setCreateBy(userId);
            logDto.setUpdateBy(userId);
        }

        sysLogProduce.send(logDto);
    }

    private JSONObject parseObject(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return JSON.parseObject(body);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 请求参数脱敏：密码、图形验证码、邮箱验证码不能落库
     */
    private String maskRequestParams(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            JSONObject json = JSON.parseObject(new String(body, StandardCharsets.UTF_8));
            SENSITIVE_FIELDS.forEach(field -> {
                if (json.containsKey(field)) {
                    json.put(field, "***");
                }
            });
            return json.toJSONString();
        } catch (Exception e) {
            return "[无法解析]";
        }
    }

    /**
     * 响应摘要只保留 code/msg/username，token 绝对不能落库
     */
    private String buildResponseSummary(JSONObject responseJson) {
        JSONObject summary = new JSONObject();
        summary.put("code", responseJson.get("code"));
        summary.put("msg", responseJson.get("msg"));
        JSONObject data = responseJson.getJSONObject("data");
        if (data != null && data.get("username") != null) {
            summary.put("username", data.get("username"));
        }
        return summary.toJSONString();
    }

    private void setBrowserInfo(LogDTO logDto, String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) {
            logDto.setOsBrowserInfo("Unknown");
            return;
        }
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            String osName = userAgent.getOperatingSystem().getName();
            String browserName = userAgent.getBrowser().getName();
            logDto.setOsBrowserInfo(osName + File.separator + browserName);
        } catch (Exception e) {
            logDto.setOsBrowserInfo("Unknown");
        }
    }
}
