package com.soft;

import com.soft.base.handle.EnvLoaderHandler;
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
        new SpringApplicationBuilder(ImbaRadishApplication.class).initializers(new EnvLoaderHandler()).run(args);
    }
}
