package com.soft.base.aspect;

import com.alibaba.fastjson2.JSON;
import com.soft.base.annotation.SysLog;
import com.soft.base.dto.LogDto;
import com.soft.base.enums.LogLevelEnum;
import com.soft.base.rabbitmq.producer.SysLogProduce;
import com.soft.base.resultapi.R;
import com.soft.base.utils.SecurityUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private final SysLogProduce sysLogProduce;

    private final HttpServletRequest servletRequest;

    private final SecurityUtil securityUtil;

    @Value(value = "${log.enable}")
    private boolean logEnable;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Autowired
    public SysLogAspect(SysLogProduce sysLogProduce, HttpServletRequest servletRequest, SecurityUtil securityUtil) {
        this.sysLogProduce = sysLogProduce;
        this.servletRequest = servletRequest;
        this.securityUtil = securityUtil;
    }

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Object result = joinPoint.proceed();
        long start = System.currentTimeMillis();
        LogDto logDto = new LogDto();
        try {
            if (logEnable) {
                logDto.setModuleName(sysLog.module().getName());
                logDto.setOperationDesc(sysLog.value());
                logDto.setType(sysLog.type().getCode());
                //joinPoint.getSignature().toShortString()
                logDto.setRequestMethod(servletRequest.getMethod());
                logDto.setRequestUrl(servletRequest.getRequestURI());
                logDto.setIpAddress(servletRequest.getRemoteAddr());

                logDto.setRequestParams(JSON.toJSONString(exclude(joinPoint)));
                logDto.setLogLevel(LogLevelEnum.INFO.getCode());
                logDto.setResponseResult(result != null ? result.toString() : null);
                logDto.setStatusCode(result != null ? ((R) result).getCode() : null);

                // 获取 User-Agent
                String userAgentString = servletRequest.getHeader(HEADER_USER_AGENT);
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                String osName = userAgent.getOperatingSystem().getName();
                String browserName = userAgent.getBrowser().getName();
                logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);

                // 解析 SpEL 表达式
                Object value = getValue(joinPoint, sysLog, parser);

                logDto.setCreateBy(value == null ? securityUtil.getUserInfo().getUsername() : String.valueOf(value));
            }
        } catch (Throwable throwable) {
            logDto.setExceptionInfo(throwable.getMessage());
            logDto.setLogLevel(LogLevelEnum.ERROR.getCode());
            throw throwable;
        } finally {
            if (logEnable) {
                logDto.setExecutionTime(System.currentTimeMillis() - start);
                sysLogProduce.sendSysLog(logDto);
            }
        }
        return result;
    }

    /**
     * 获取value值
     *
     * @param joinPoint
     * @param sysLog
     * @param parser
     * @return
     */
    private Object getValue(ProceedingJoinPoint joinPoint, SysLog sysLog, SpelExpressionParser parser) {
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        // 构建 SpEL 解析上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 查找指定的参数
        String targetParam = sysLog.param(); // 获取注解中指定的参数名
        Object targetValue;
        if (StringUtils.isBlank(targetParam)) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]); // 设置参数名和对应值
            }
        } else {
            for (int i = 0; i < paramNames.length; i++) {
                if (paramNames[i].equals(targetParam)) {
                    targetValue = args[i]; // 找到匹配的参数值
                    context.setVariable(targetParam, targetValue); // 仅设置指定参数
                    break;
                }
            }
        }

        String spelExpression = sysLog.name();
        if (StringUtils.isNotBlank(spelExpression)) {
            Expression expression = parser.parseExpression(spelExpression);
            return expression.getValue(context);
        }
        return null;
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
