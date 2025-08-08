package com.soft;

import com.soft.base.core.handle.EnvLoaderHandler;
import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.soft.cyx.mapper","com.soft.base.mapper"})
@EnableCaching
@EnableScheduling
public class ImbaRadishApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder initializers = new SpringApplicationBuilder(ImbaRadishApplication.class);
        initializers.initializers(new EnvLoaderHandler());
        initializers.run(args);
    }
}
