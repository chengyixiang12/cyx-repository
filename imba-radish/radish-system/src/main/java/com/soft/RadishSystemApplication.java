package com.soft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统设置服务启动类。
 *
 * <p>阶段一为空骨架，仅加载 Web 与 Actuator。待 {@code com.soft.sys} 的业务代码
 * 从 radish-monolithic 迁入后，本模块将成为系统设置服务的宿主。
 *
 * <p>扫描范围固定为 {@code com.soft.system}，避免误扫到其他模块的组件。
 *
 * @author cyx
 */
@SpringBootApplication(scanBasePackages = {"com.soft.system"})
public class RadishSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadishSystemApplication.class, args);
    }
}
