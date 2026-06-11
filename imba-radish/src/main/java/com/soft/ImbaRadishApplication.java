package com.soft;

import com.soft.sys.core.handle.EnvLoaderHandler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ImbaRadishApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder initializers = new SpringApplicationBuilder(ImbaRadishApplication.class);
        initializers.initializers(new EnvLoaderHandler());
        initializers.run(args);
    }
}
