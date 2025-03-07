package com.soft.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/7 11:10
 **/

@Component
@Data
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperty {

    private Boolean enable;

    private Integer maxRequest;

    private Long windowSize;
}
