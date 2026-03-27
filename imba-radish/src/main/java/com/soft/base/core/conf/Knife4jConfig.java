package com.soft.base.core.conf;

import com.soft.base.properties.AuthorizationIgnoreProperty;
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
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class Knife4jConfig {

    private final AuthorizationIgnoreProperty authorizationIgnoreProperty;

    private final PathMatcher pathMatcher;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("接口文档") // 修改为你想要的标题
                        .version("1.0.0") // 版本
                        .description("API Description").contact(new Contact()
                                .name("cyx")
                                .email("1574641450@qq.com")))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                        new SecurityScheme()
                                .name(HttpHeaders.AUTHORIZATION)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("Bearer")
                                .bearerFormat("JWT")));
    }

    /**
     * 配置全局请求头参数
     *
     * @return
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            Paths paths = openApi.getPaths();
            if (paths == null) {
                return;
            }
            paths.entrySet().stream()
                    .filter(entry -> !checkPermitUrl(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .flatMap(pathItem -> pathItem.readOperations().stream())
                    .forEach(operation ->
                            operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                    );
        };
    }

    /**
     * 校验不鉴权url
     * @param uri
     * @return
     */
    private boolean checkPermitUrl(String uri) {
        List<String> notPermitUrl = authorizationIgnoreProperty.getUrls();
        for (String pattern : notPermitUrl) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
