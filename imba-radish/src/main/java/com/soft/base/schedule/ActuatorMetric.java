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

        // 1. system.cpu.usage - CPU使用率（0-1之间的浮点值）
        Double cpuUsage = getGaugeValue("system.cpu.usage");
        // 2. system.cpu.count - CPU逻辑核心数
        Double cpuCount = getGaugeValue("system.cpu.count");
        // 3. jvm.memory.max - 堆最大内存（字节）
        Double memoryHeapMax = getGaugeValue("jvm.memory.max", Tags.of("area", "heap"));
        // 4. jvm.memory.used - 堆已使用内存（字节）
        Double memoryHeapUsed = getGaugeValue("jvm.memory.used", Tags.of("area", "heap"));
        // 5. disk.free - 磁盘可用空间（字节）
        Double diskFree = getGaugeValue("disk.free");
        // 6. disk.total - 磁盘总空间（字节）
        Double diskTotal = getGaugeValue("disk.total");
        // 7. process.uptime - 进程运行时间（秒）
        Double uptime = getGaugeValue("process.uptime");
        // 8. jvm.memory.max - 非堆最大内存（字节）
        Double memoryNoheapMax = getGaugeValue("jvm.memory.max", Tags.of("area", "nonheap"));
        // 9. jvm.memory.used - 非堆已使用内存（字节）
        Double memoryNoheapUsed = getGaugeValue("jvm.memory.used", Tags.of("area", "nonheap"));
        // 11. jvm.memory.used - G1 Eden区已使用内存（字节）
        Double memoryG1EdenUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Eden Space"));
        // 13. jvm.memory.used - G1 Survivor区已使用内存（字节）
        Double memoryG1SurvivorUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Survivor Space"));
        // 14. jvm.memory.max - G1老年代最大内存（字节）
        Double memoryG1OldMax = getGaugeValue("jvm.memory.max", Tags.of("id", "G1 Old Gen"));
        // 15. jvm.memory.used - G1老年代已使用内存（字节）
        Double memoryG1OldUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Old Gen"));
        // 16. jvm.memory.max - CodeCache最大内存（字节）
        Double memoryCodeCacheMax = getGaugeValue("jvm.memory.max", Tags.of("id", "CodeCache"));
        // 17. jvm.memory.used - CodeCache已使用内存（字节）
        Double memoryCodeCacheUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "CodeCache"));
        // 19. jvm.memory.used - Metaspace已使用内存（字节）
        Double memoryMetaspaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Metaspace"));
        // 20. jvm.memory.max - Compressed Class Space最大内存（字节）
        Double memoryCompressClassSpaceMax = getGaugeValue("jvm.memory.max", Tags.of("id", "Compressed Class Space"));
        // 21. jvm.memory.used - Compressed Class Space已使用内存（字节）
        Double memoryCompressClassSpaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Compressed Class Space"));

        sysActuator.setCreateTime(LocalDateTime.now());
        sysActuator.setCpuUsage(cpuUsage);
        sysActuator.setCpuCount(cpuCount != null ? cpuCount.intValue() : null);
        sysActuator.setMemoryHeapMax(memoryHeapMax != null ? memoryHeapMax.longValue() : null);
        sysActuator.setMemoryHeapUsed(memoryHeapUsed != null ? memoryHeapUsed.longValue() : null);
        sysActuator.setMemoryNoheapMax(memoryNoheapMax != null ? memoryNoheapMax.longValue() : null);
        sysActuator.setMemoryNoheapUsed(memoryNoheapUsed != null ? memoryNoheapUsed.longValue() : null);
        sysActuator.setMemoryG1EdenUsed(memoryG1EdenUsed != null ? memoryG1EdenUsed.longValue() : null);
        sysActuator.setMemoryG1SurvivorUsed(memoryG1SurvivorUsed != null ? memoryG1SurvivorUsed.longValue() : null);
        sysActuator.setMemoryG1OldMax(memoryG1OldMax != null ? memoryG1OldMax.longValue() : null);
        sysActuator.setMemoryG1OldUsed(memoryG1OldUsed != null ? memoryG1OldUsed.longValue() : null);
        sysActuator.setMemoryCodeCacheMax(memoryCodeCacheMax != null ? memoryCodeCacheMax.longValue() : null);
        sysActuator.setMemoryCodeCacheUsed(memoryCodeCacheUsed != null ? memoryCodeCacheUsed.longValue() : null);
        sysActuator.setMemoryMetaspaceUsed(memoryMetaspaceUsed != null ? memoryMetaspaceUsed.longValue() : null);
        sysActuator.setMemoryCompressClassSpaceMax(memoryCompressClassSpaceMax != null ? memoryCompressClassSpaceMax.longValue() : null);
        sysActuator.setMemoryCompressClassSpaceUsed(memoryCompressClassSpaceUsed != null ? memoryCompressClassSpaceUsed.longValue() : null);
        sysActuator.setDiskFree(diskFree != null ? diskFree.longValue() : null);
        sysActuator.setDiskTotal(diskTotal != null ? diskTotal.longValue() : null);
        sysActuator.setUptime(uptime != null ? uptime.longValue() : null);

        // 总内存使用 = 堆已使用 + 非堆已使用
        Long totalMemoryUsed = null;
        if (memoryHeapUsed != null && memoryNoheapUsed != null) {
            totalMemoryUsed = memoryHeapUsed.longValue() + memoryNoheapUsed.longValue();
        }

        // 总内存最大 = 堆最大 + 非堆最大
        Long totalMemoryMax = null;
        if (memoryHeapMax != null && memoryNoheapMax != null) {
            totalMemoryMax = memoryHeapMax.longValue() + memoryNoheapMax.longValue();
        }

        sysActuator.setMemoryUsed(totalMemoryUsed);
        sysActuator.setMemoryMax(totalMemoryMax);

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