package com.soft.base.core.aspect;

import com.alibaba.fastjson2.JSON;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogLevelEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.dto.LogDto;
import com.soft.base.rabbitmq.producer.SysLogProduce;
import com.soft.base.resultapi.R;
import com.soft.base.utils.SecurityUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.soft.base.constants.BaseConstant.HEADER_USER_AGENT;
import static com.soft.base.constants.BaseConstant.LEFT_SLASH;


/**
 * @Author: cyx
 * @Description: 日志拦截器
 * @DateTime: 2024/11/21 10:48
 **/


@Slf4j
@Aspect
@Component
@Order(9)
public class SysLogAspect {

    @Value(value = "${log.enable}")
    private boolean logEnable;

    @Value(value = "${log.record-param}")
    private boolean recordParam;

    private final SysLogProduce sysLogProduce;

    private final HttpServletRequest servletRequest;

    private final SecurityUtil securityUtil;

    @Autowired
    public SysLogAspect(SysLogProduce sysLogProduce, HttpServletRequest servletRequest, SecurityUtil securityUtil) {
        this.sysLogProduce = sysLogProduce;
        this.servletRequest = servletRequest;
        this.securityUtil = securityUtil;
    }

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        if (logEnable) {
            LogDto logDto = new LogDto();
            try {
                logDto.setModuleName(sysLog.module().getName());
                logDto.setOperationDesc(sysLog.value());
                logDto.setType(sysLog.type().getCode());
                //joinPoint.getSignature().toShortString()
                logDto.setRequestMethod(servletRequest.getMethod());
                logDto.setRequestUrl(servletRequest.getRequestURI());
                logDto.setIpAddress(servletRequest.getRemoteAddr());

                // 是否记录请求和响应参数
                if (recordParam) {
                    logDto.setRequestParams(JSON.toJSONString(exclude(joinPoint)));
                    logDto.setResponseResult(result != null ? result.toString() : null);
                }

                if (result != null) {
                    @SuppressWarnings("unchecked")
                    R<Object> r = (R<Object>) result;
                    Integer code = r.getCode();

                    logDto.setStatusCode(code);
                    logDto.setLogLevel(LogLevelEnum.INFO.getCode());
                }

                // 获取 User-Agent
                String userAgentString = servletRequest.getHeader(HEADER_USER_AGENT);
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                String osName = userAgent.getOperatingSystem().getName();
                String browserName = userAgent.getBrowser().getName();
                logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);

                logDto.setCreateBy(securityUtil.getUserInfo().getId());
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
                sysLogProduce.sendSysLog(logDto);
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
        for (int i = 0; i < args.size(); ) {
            if (args.get(i) instanceof MultipartFile) {
                args.remove(i);
                continue;
            }
            i++;
        }
        return args;
    }
}
