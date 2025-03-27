package com.soft.base.aspect;

import com.alibaba.fastjson2.JSON;
import com.soft.base.annotation.SysLog;
import com.soft.base.model.dto.LogDto;
import com.soft.base.enums.LogLevelEnum;
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
        Object result = joinPoint.proceed();
        if (logEnable) {
            long start = System.currentTimeMillis();
            LogDto logDto = new LogDto();
            try {
                logDto.setModuleName(sysLog.module().getName());
                logDto.setOperationDesc(sysLog.value());
                logDto.setType(sysLog.type().getCode());
                //joinPoint.getSignature().toShortString()
                logDto.setRequestMethod(servletRequest.getMethod());
                logDto.setRequestUrl(servletRequest.getRequestURI());
                logDto.setIpAddress(servletRequest.getRemoteAddr());
                logDto.setLogLevel(LogLevelEnum.INFO.getCode());

                // 是否记录请求和响应参数
                if (recordParam) {
                    logDto.setRequestParams(JSON.toJSONString(exclude(joinPoint)));
                    logDto.setResponseResult(result != null ? result.toString() : null);
                }

                // 记录接口响应代码
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    Integer code = ((R<Object>) result).getCode();
                    logDto.setStatusCode(code);
                }

                // 获取 User-Agent
                String userAgentString = servletRequest.getHeader(HEADER_USER_AGENT);
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                String osName = userAgent.getOperatingSystem().getName();
                String browserName = userAgent.getBrowser().getName();
                logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);

                logDto.setCreateBy(securityUtil.getUserInfo().getId());
            } catch (Throwable throwable) {
                logDto.setExceptionInfo(throwable.getMessage());
                logDto.setLogLevel(LogLevelEnum.ERROR.getCode());
                throw throwable;
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
