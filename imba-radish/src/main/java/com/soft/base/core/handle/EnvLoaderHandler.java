package com.soft.base.core.handle;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/15 10:55
 **/
public class EnvLoaderHandler implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try (InputStream is = new FileInputStream(".env")) {
            Properties props = new Properties();
            props.load(is);

            Map<String, Object> map = new HashMap<>();
            props.forEach((key, value) -> map.put(String.valueOf(key), value));

            // 加入 Spring 的 Environment 中
            MapPropertySource propertySource = new MapPropertySource("custom-env", map);
            context.getEnvironment().getPropertySources().addFirst(propertySource);
        } catch (IOException e) {
            throw new RuntimeException(".env文件不存在", e);
        }
    }
}
