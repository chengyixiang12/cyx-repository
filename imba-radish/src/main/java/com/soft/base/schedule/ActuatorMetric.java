package com.soft.base.schedule;

import com.soft.base.entity.SysActuator;
import com.soft.base.service.SysActuatorService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description: 采集系统监控指标（CPU、内存、磁盘、运行时间及各组件健康状态）
 * @date 2026-04-30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActuatorMetric {

    private final MeterRegistry meterRegistry;

    private final SysActuatorService sysActuatorService;

    private final HealthEndpoint healthEndpoint;

    @Scheduled(cron = "0 */1 * * * *")
    public void run() {
        SysActuator sysActuator = new SysActuator();
        sysActuator.setCreateTime(LocalDateTime.now());  // 设置创建时间

        // 1. system.cpu.usage - CPU使用率（0-1之间的浮点值）
        Double cpuUsage = getGaugeValue("system.cpu.usage");
        sysActuator.setCpuUsage(cpuUsage);

        // 2. system.cpu.count - CPU逻辑核心数
        Double cpuCount = getGaugeValue("system.cpu.count");
        sysActuator.setCpuCount(cpuCount != null ? cpuCount.intValue() : null);

        // 3. jvm.memory.max - JVM堆最大内存（字节）
        Double jvmMemoryMax = getGaugeValue("jvm.memory.max");
        sysActuator.setMemeryMax(jvmMemoryMax != null ? jvmMemoryMax.longValue() : null);

        // 4. jvm.memory.used - JVM堆已使用内存（字节）
        Double jvmMemoryUsed = getGaugeValue("jvm.memory.used");
        sysActuator.setMemeryUsed(jvmMemoryUsed != null ? jvmMemoryUsed.longValue() : null);

        // 5. disk.free - 磁盘可用空间（字节）
        Double diskFree = getGaugeValue("disk.free");
        sysActuator.setDiskFree(diskFree != null ? diskFree.longValue() : null);

        // 6. disk.total - 磁盘总空间（字节）
        Double diskTotal = getGaugeValue("disk.total");
        sysActuator.setDiskTotal(diskTotal != null ? diskTotal.longValue() : null);

        // 7. process.uptime - 进程运行时间（指标单位：秒）
        Double uptime = getGaugeValue("process.uptime");
        sysActuator.setUptime(uptime != null ? uptime.longValue() : null);

        // 8. 整体健康状态
        HealthComponent health = healthEndpoint.health();
        sysActuator.setHealth(health.getStatus().getCode());

        // 9. 各组件健康状态（使用 healthForPath 直接获取，更准确）
        sysActuator.setHealthDb(getComponentStatus("db"));
        sysActuator.setHealthRedis(getComponentStatus("redis"));
//        sysActuator.setHealthMail(getComponentStatus("mail"));
        sysActuator.setHealthRabbit(getComponentStatus("rabbit"));
        sysActuator.setHealthSsl(getComponentStatus("ssl"));
        sysActuator.setHealthDiskSpace(getComponentStatus("diskSpace"));
        sysActuator.setHealthPing(getComponentStatus("ping"));

        // 存入数据库
        sysActuatorService.save(sysActuator);
    }

    /**
     * 获取单个组件的健康状态代码（如 "UP"、"DOWN"）
     */
    private String getComponentStatus(String componentName) {
        try {
            HealthComponent component = healthEndpoint.healthForPath(componentName);
            return component.getStatus().getCode();
        } catch (Exception e) {
            log.debug("无法获取组件 {} 的健康状态，原因：{}", componentName, e.getMessage());
            return "UNKNOWN";
        }
    }

    /**
     * 获取 Gauge 指标值（无标签）
     */
    private Double getGaugeValue(String metricName) {
        return getGaugeValue(metricName, Tags.empty());
    }

    /**
     * 获取带标签的 Gauge 指标值
     */
    private Double getGaugeValue(String metricName, Tags tags) {
        try {
            var gauge = meterRegistry.find(metricName).tags(tags).gauge();
            if (gauge != null) {
                return gauge.value();
            }
        } catch (Exception e) {
            log.error("获取指标 {} 失败", metricName, e);
        }
        return null;
    }
}