package com.soft.sys.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author cyx
 * @date 2026-06-10
 */

@Component
@Data
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperty {

    /**
     * 源
     */
    private List<String> allowedOrigins;
}
