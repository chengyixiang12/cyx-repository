package com.soft.sys.core.conf;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.TimeZone;

/**
 * Jackson 全局序列化配置
 *
 * @author cyx
 * @date 2026-05-09
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DEFAULT_TIME_ZONE = "GMT+8";

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                // 空字符串（""）反序列化为 null
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                // float/double 使用 BigDecimal 接收，避免浮点精度丢失
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                // === 全局 ===
                // 序列化时忽略 null 字段
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL).withValueInclusion(JsonInclude.Include.NON_NULL))
                // 默认时区，与 @JsonFormat 保持一致
                .defaultTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE))
                .build();
    }
}
