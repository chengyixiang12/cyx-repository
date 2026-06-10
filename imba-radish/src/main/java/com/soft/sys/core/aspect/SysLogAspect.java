package com.soft.sys.core.aspect;

import com.alibaba.fastjson2.JSON;
import com.soft.sys.core.annotation.LogIgnore;
import com.soft.sys.core.annotation.SysLog;
import com.soft.sys.enums.LogLevelEnum;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.model.dto.LogDto;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.rabbitmq.producer.SysLogProduce;
import com.soft.sys.resultapi.R;
import com.soft.sys.utils.SecurityUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.soft.sys.constants.BaseConstant.LEFT_SLASH;


/**
 * @Author: cyx
 * @Description: 日志拦截器
 * @DateTime: 2024/11/21 10:48
 **/


@Slf4j
@Aspect
@Component
@Order(99)
@RequiredArgsConstructor
public class SysLogAspect {

    private final RadishProperty radishProperty;

    private final SysLogProduce sysLogProduce;

    private final SecurityUtil securityUtil;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        if (!radishProperty.getLog().getEnable()) {
            return joinPoint.proceed();
        }

        long start = System.currentTimeMillis();
        LogDto logDto = new LogDto();
        try {
            Object result = joinPoint.proceed();
            populateLogDto(joinPoint, sysLog, logDto, result, null);
            return result;
        } catch (Throwable throwable) {
            populateLogDto(joinPoint, sysLog, logDto, null, throwable);
            throw throwable;
        } finally {
            logDto.setExecutionTime(System.currentTimeMillis() - start);
            sysLogProduce.send(logDto);
        }
    }

    private void populateLogDto(ProceedingJoinPoint joinPoint, SysLog sysLog, LogDto logDto, Object result, Throwable throwable) {
        logDto.setModuleName(sysLog.module().getName());
        logDto.setOperationDesc(sysLog.value());
        logDto.setType(sysLog.type().getCode());

        HttpServletRequest servletRequest = getServletRequest();
        if (servletRequest != null) {
            logDto.setRequestMethod(servletRequest.getMethod());
            logDto.setRequestUrl(servletRequest.getRequestURI());
            logDto.setIpAddress(getClientIp(servletRequest));

            if (sysLog.requestParam()) {
                logDto.setRequestParams(JSON.toJSONString(exclude(joinPoint)));
            }

            setBrowserInfo(logDto, servletRequest.getHeader("User-Agent"));
        }

        if (throwable == null) {
            handleResponseResult(result, logDto, sysLog.responseParam());
            if (logDto.getLogLevel() == null) {
                logDto.setLogLevel(LogLevelEnum.INFO.getCode());
            }
        } else {
            logDto.setLogLevel(LogLevelEnum.ERROR.getCode());
            logDto.setExceptionInfo(throwable.getMessage());
        }

        if (securityUtil != null && securityUtil.getUserInfo() != null) {
            logDto.setCreateBy(securityUtil.getUserInfo().getId());
            logDto.setUpdateBy(securityUtil.getUserInfo().getId());
        }
    }

    private void handleResponseResult(Object result, LogDto logDto, boolean responseFlag) {
        if (result == null) {
            return;
        }

        if (result instanceof R<?> r) {
            logDto.setStatusCode(r.getCode());
            if (responseFlag) {
                logDto.setResponseResult(formatResponseData(r.getData()));
            }
            return;
        }

        if (responseFlag) {
            logDto.setResponseResult(formatResponseData(result));
        }
    }

    private String formatResponseData(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof byte[]) {
            return "[二进制数据, 长度:" + ((byte[]) data).length + " bytes]";
        }
        if (data instanceof InputStream || data instanceof MultipartFile) {
            return "[流数据]";
        }
        if (data instanceof String stringData) {
            return stringData.length() > 2000 ? stringData.substring(0, 2000) + "...[截断]" : stringData;
        }
        try {
            String json = JSON.toJSONString(data);
            return json.length() > 2000 ? json.substring(0, 2000) + "...[截断]" : json;
        } catch (Exception e) {
            return "[无法序列化]";
        }
    }

    private HttpServletRequest getServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        header = request.getHeader("X-Real-IP");
        if (header != null && !header.isBlank()) {
            return header;
        }
        return request.getRemoteAddr();
    }

    private void setBrowserInfo(LogDto logDto, String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) {
            logDto.setOsBrowserInfo("Unknown");
            return;
        }
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            String osName = userAgent.getOperatingSystem().getName();
            String browserName = userAgent.getBrowser().getName();
            logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);
        } catch (Exception e) {
            logDto.setOsBrowserInfo("Unknown");
        }
    }

    /**
     * 排除
     *
     * @param joinPoint
     * @return
     */
    private List<Object> exclude(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        List<Object> filteredArgs = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            if (isExcludedArg(arg)) {
                continue;
            }
            if (hasLogIgnoreAnnotation(parameterAnnotations, i)) {
                continue;
            }
            filteredArgs.add(arg);
        }
        return filteredArgs;
    }

    private boolean isExcludedArg(Object arg) {
        for (Class<?> excludedType : EXCLUDED_PARAM_TYPES) {
            if (excludedType.isInstance(arg)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLogIgnoreAnnotation(Annotation[][] parameterAnnotations, int index) {
        if (parameterAnnotations == null || index < 0 || index >= parameterAnnotations.length) {
            return false;
        }
        for (Annotation annotation : parameterAnnotations[index]) {
            if (annotation.annotationType() == LogIgnore.class) {
                return true;
            }
        }
        return false;
    }

    /**
     *  参数类型黑名单
     */
    private static final Set<Class<?>> EXCLUDED_PARAM_TYPES = Set.of(
            MultipartFile.class
    );
}
