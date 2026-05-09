package com.soft.base.core.aspect;

import com.alibaba.fastjson2.JSON;
import com.soft.base.core.annotation.LogIgnore;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogLevelEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.dto.LogDto;
import com.soft.base.properties.RadishProperty;
import com.soft.base.rabbitmq.producer.SysLogProduce;
import com.soft.base.resultapi.R;
import com.soft.base.utils.SecurityUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;

import static com.soft.base.constants.BaseConstant.LEFT_SLASH;


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
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        if (radishProperty.getLog().getEnable()) {
            HttpServletRequest servletRequest = ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
            LogDto logDto = new LogDto();
            try {
                logDto.setModuleName(sysLog.module().getName());
                logDto.setOperationDesc(sysLog.value());
                logDto.setType(sysLog.type().getCode());
                //joinPoint.getSignature().toShortString()
                logDto.setRequestMethod(servletRequest.getMethod());
                logDto.setRequestUrl(servletRequest.getRequestURI());
                logDto.setIpAddress(servletRequest.getRemoteAddr());

                // 是否记录请求参数
                boolean requestFlag = sysLog.requestParam();
                if (requestFlag) {
                    logDto.setRequestParams(JSON.toJSONString(exclude(joinPoint)));
                }

                if (result != null) {
                    @SuppressWarnings("unchecked")
                    R<Object> r = (R<Object>) result;
                    Object data = r.getData();
                    Integer code = r.getCode();
                    String responseResult;

                    if (data == null) {
                        responseResult = null;
                    } else if (data instanceof byte[]) {
                        responseResult = "[二进制数据, 长度:" + ((byte[]) data).length + " bytes]";
                    } else if (data instanceof InputStream || data instanceof MultipartFile) {
                        responseResult = "[流数据]";
                    } else if (data instanceof String) {
                        responseResult = ((String) data).length() > 2000
                                ? ((String) data).substring(0, 2000) + "...[截断]"
                                : (String) data;
                    } else {
                        try {
                            String json = JSON.toJSONString(data);
                            if (json.length() > 2000) {
                                responseResult = json.substring(0, 2000) + "...[截断]";
                            } else {
                                responseResult = json;
                            }
                        } catch (Exception e) {
                            responseResult = "[无法序列化]";
                        }
                    }

                    logDto.setStatusCode(code);
                    logDto.setLogLevel(LogLevelEnum.INFO.getCode());

                    // 是否记录响应参数
                    boolean responseFlag = sysLog.responseParam();
                    if (responseFlag) {
                        logDto.setResponseResult(responseResult);
                    }
                }

                // 获取 User-Agent
                String userAgentString = servletRequest.getHeader("User-Agent");
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                String osName = userAgent.getOperatingSystem().getName();
                String browserName = userAgent.getBrowser().getName();
                logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);

                logDto.setCreateBy(securityUtil.getUserInfo().getId());
                logDto.setUpdateBy(securityUtil.getUserInfo().getId());
            } catch (GlobalException e) {
                logDto.setLogLevel(LogLevelEnum.ERROR.getCode());
                logDto.setExceptionInfo(e.getLocalizedMessage());
                throw new GlobalException(e.getLocalizedMessage());
            } catch (Exception e) {
                logDto.setLogLevel(LogLevelEnum.ERROR.getCode());
                logDto.setExceptionInfo(e.getLocalizedMessage());
                throw new Exception(e);
            } finally {
                logDto.setExecutionTime(System.currentTimeMillis() - start);
                sysLogProduce.send(logDto);
            }
        }

        return result;
    }

    /**
     * 排除
     *
     * @param joinPoint
     * @return
     */
    private List<Object> exclude(ProceedingJoinPoint joinPoint) {
        List<Object> args = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        args.removeIf(item -> {
            if (item == null) return false;

            if (EXCLUDED_PARAM_TYPES.contains(item.getClass())) return true;

            Annotation[] annotations = item.getClass().getAnnotations();
            for (Annotation ann : annotations) {
                if (ann.annotationType() == LogIgnore.class) return true;
            }

            return false;
        });
        return args;
    }

    /**
     *  参数类型黑名单
     */
    private static final Set<Class<?>> EXCLUDED_PARAM_TYPES = Set.of(
            MultipartFile.class
    );
}
