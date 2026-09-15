package com.soft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 业务服务启动类。
 *
 * <p>阶段一为空骨架，仅加载 Web 与 Actuator。待 {@code com.soft.module} 的业务代码
 * 从 radish-monolithic 迁入后，本模块将成为业务服务的宿主。
 *
 * <p>扫描范围固定为 {@code com.soft.business}，避免误扫到其他模块的组件。
 *
 * @author cyx
 */
@SpringBootApplication(scanBasePackages = {"com.soft.business"})
public class RadishBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadishBusinessApplication.class, args);
    }
}
