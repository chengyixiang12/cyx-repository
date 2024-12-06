package com.soft.base.conf;

import com.soft.base.constants.BaseConstant;
import com.soft.base.properties.AuthorizationIgnoreProperty;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Configuration
public class Knife4jConfig {

    private AuthorizationIgnoreProperty authorizationIgnoreProperty;

    @Autowired
    public void setPermits(AuthorizationIgnoreProperty authorizationIgnoreProperty) {
        this.authorizationIgnoreProperty = authorizationIgnoreProperty;
    }

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
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    if (checkPermitUrl(s)) return;
                    // 接口添加鉴权参数
                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                            );
                });
            }
        };
    }

    /**
     * 校验不鉴权url
     * todo 还有单通配未实现
     * @param uri
     * @return
     */
    private boolean checkPermitUrl(String uri) {
        List<String> notPermitUrl = authorizationIgnoreProperty.getUrls();
        for (String c : notPermitUrl) {
            if (BaseConstant.ALL_WILDCARD_CHARACTER.equals(c.substring(c.length() - 2)) &&
                    uri.length() > c.length() &&
                    uri.substring(0, c.length() - 2)
                            .equals(c.replaceAll(BaseConstant.ESCAPE_CHARACTER + BaseConstant.ALL_WILDCARD_CHARACTER, BaseConstant.BLANK_CHARACTER))) {
                return true;
            } else if (uri.equals(c)) {
                return true;
            }
        }
        return false;
    }
}
