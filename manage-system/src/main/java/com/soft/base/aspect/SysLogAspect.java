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

    @Autowired
    public SysLogAspect(SysLogProduce sysLogProduce, HttpServletRequest servletRequest, SecurityUtil securityUtil) {
        this.sysLogProduce = sysLogProduce;
        this.servletRequest = servletRequest;
        this.securityUtil = securityUtil;
    }

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Object result = null;
        if (logEnable) {
            long start = System.currentTimeMillis();
            LogDto logDto = new LogDto();
            try {
                SpelExpressionParser parser = new SpelExpressionParser();
                logDto.setModuleName(sysLog.module().getName());
                logDto.setOperationDesc(sysLog.value());
                logDto.setType(sysLog.type().getCode());
                //joinPoint.getSignature().toShortString()
                logDto.setRequestMethod(servletRequest.getMethod());
                logDto.setRequestUrl(servletRequest.getRequestURL().toString());
                logDto.setIpAddress(servletRequest.getRemoteAddr());
                logDto.setRequestParams(JSON.toJSONString(joinPoint.getArgs()));
                logDto.setLogLevel(LogLevelEnum.INFO.getCode());

                result = joinPoint.proceed();
                logDto.setResponseResult(result != null ? result.toString() : null);
                logDto.setStatusCode(result != null ? ((R)result).getCode() : null);

                // 获取 User-Agent
                String userAgentString = servletRequest.getHeader(HEADER_USER_AGENT);
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
                String osName = userAgent.getOperatingSystem().getName();
                String browserName = userAgent.getBrowser().getName();
                logDto.setOsBrowserInfo(osName + LEFT_SLASH + browserName);

                StandardEvaluationContext context = getStandardEvaluationContext(joinPoint, sysLog);
                // 解析 SpEL 表达式
                String spelExpression = sysLog.name();
                Expression expression = parser.parseExpression(spelExpression);
                logDto.setCreateBy(expression.getValue(context) == null ? securityUtil.getUserInfo().getUsername() : String.valueOf(expression.getValue(context)));
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

    @NotNull
    private StandardEvaluationContext getStandardEvaluationContext(ProceedingJoinPoint joinPoint, SysLog sysLog) {
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
        return context;
    }
}
