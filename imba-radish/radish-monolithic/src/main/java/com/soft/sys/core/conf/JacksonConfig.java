package com.soft.sys.core.conf;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson 全局序列化配置
 *
 * <p>通过 Spring Boot 的 {@link JsonMapperBuilderCustomizer} 扩展自动配置的 JsonMapper。
 * 不要直接声明 {@code ObjectMapper} Bean：Boot 4 自动配置的是 {@code JsonMapper}，
 * 两者并存时 MVC 仍使用自动配置的 {@code @Primary JsonMapper}，自定义的 {@code ObjectMapper} 不会生效。
 *
 * <p>已在此统一注册 Java 时间类型的全局格式，业务字段无需再逐个添加 {@code @JsonFormat}；
 * 个别字段若需特殊格式，{@code @JsonFormat} 的优先级更高，仍可覆盖全局配置。
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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                // 空字符串（""）反序列化为 null
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                // float/double 使用 BigDecimal 接收，避免浮点精度丢失
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                // === 全局 ===
                // 序列化时忽略 null 字段
                .changeDefaultPropertyInclusion(incl -> incl
                        .withContentInclusion(JsonInclude.Include.NON_NULL)
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                // 默认时区，与 @JsonFormat 保持一致
                .defaultTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE))
                // 全局 Java 时间格式，替代字段上的 @JsonFormat
                .addModule(javaTimeModule());
    }

    /**
     * 注册 LocalDateTime / LocalDate / LocalTime 的全局格式。
     * 自定义序列化器优先于 Jackson 内建的 JavaTime 默认实现。
     */
    private SimpleModule javaTimeModule() {
        SimpleModule module = new SimpleModule("CustomJavaTimeModule");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        return module;
    }
}
