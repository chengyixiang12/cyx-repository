package com.soft.sys.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "spring.security.permit")
public class AuthorizationIgnoreProperty {

    List<String> url;
}
