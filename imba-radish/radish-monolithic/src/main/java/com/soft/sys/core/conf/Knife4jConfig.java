package com.soft.sys.core.conf;

import com.soft.sys.properties.AuthorizationIgnoreProperty;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.PathMatcher;

import java.util.List;

/**
 * Knife4j / SpringDoc 接口文档配置
 *
 * @author cyx
 */
@Configuration
@RequiredArgsConstructor
public class Knife4jConfig {

    private final AuthorizationIgnoreProperty authorizationIgnoreProperty;
    private final PathMatcher pathMatcher;

    /**
     * OpenAPI 文档基本信息与 JWT 安全方案
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("接口文档")
                        .version("1.0.0")
                        .description("API Description")
                        .contact(new Contact()
                                .name("cyx")
                                .email("1574641450@qq.com")))
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                                .name(HttpHeaders.AUTHORIZATION)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("Bearer")
                                .bearerFormat("JWT")));
    }

    /**
     * 全局 OpenAPI 定制器：对非白名单接口自动注入 Authorization 鉴权参数
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) {
                return;
            }
            paths.forEach((uri, pathItem) -> {
                if (!isPermitUrl(uri)) {
                    pathItem.readOperations().forEach(operation ->
                            operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION)));
                }
            });
        };
    }

    /**
     * 判断指定 URI 是否在鉴权白名单中
     *
     * @param uri 请求路径
     * @return true 表示免鉴权
     */
    private boolean isPermitUrl(String uri) {
        List<String> permitUrls = authorizationIgnoreProperty.getUrl();
        if (permitUrls == null || permitUrls.isEmpty()) {
            return false;
        }
        return permitUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
}
