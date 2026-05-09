package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/2/7 11:10
 **/

@Component
@Data
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperty {

    private Boolean enable;

    private Long maxRequest;

    private Long windowSize;

    private Permit permit = new Permit();

    @Data
    public static class Permit {

        private List<String> urls;
    }
}
