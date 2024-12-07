package com.soft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.soft.cyx.mapper","com.soft.base.mapper"})
@EnableCaching
@EnableScheduling
public class ManageSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageSystemApplication.class, args);
    }
}
