package com.soft;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.soft.cyx.mapper","com.soft.base.mapper"})
@EnableCaching
@EnableScheduling
public class ImbaRadishApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImbaRadishApplication.class, args);
    }

    /**
     * 加载环境变量
     */
    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().parallelStream().forEach((e -> System.setProperty(e.getKey(), e.getValue())));
    }
}
