package com.soft.sys.schedule;

import com.soft.sys.entity.SysActuator;
import com.soft.sys.service.SysActuatorService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        // 8. jvm.memory.used - Metaspace已使用内存（字节）
        Double memoryMetaspaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Metaspace"));
        // 9. jvm.memory.used - G1 Eden区已使用内存（字节）
        Double memoryG1EdenUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Eden Space"));
        // 10. jvm.memory.used - G1 Survivor区已使用内存（字节）
        Double memoryG1SurvivorUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Survivor Space"));
        // 12. jvm.memory.used - G1老年代已使用内存（字节）
        Double memoryG1OldUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Old Gen"));
        // 13. jvm.memory.max - CodeCache最大内存（字节）
        Double memoryCodeCacheMax = getGaugeValue("jvm.memory.max", Tags.of("id", "CodeCache"));
        // 14. jvm.memory.used - CodeCache已使用内存（字节）
        Double memoryCodeCacheUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "CodeCache"));
        // 15. jvm.memory.max - Metaspace最大内存（字节）
        Double memoryMetaspaceMax = getGaugeValue("jvm.memory.max", Tags.of("id", "Metaspace"));
        // 16. jvm.memory.max - Compressed Class Space最大内存（字节）
        Double memoryCompressClassSpaceMax = getGaugeValue("jvm.memory.max", Tags.of("id", "Compressed Class Space"));
        // 17. jvm.memory.used - Compressed Class Space已使用内存（字节）
        Double memoryCompressClassSpaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Compressed Class Space"));

        sysActuator.setCpuUsage(cpuUsage);
        sysActuator.setCpuCount(cpuCount != null ? cpuCount.intValue() : null);
        sysActuator.setMemoryHeapMax(memoryHeapMax != null ? memoryHeapMax.longValue() : null);
        sysActuator.setMemoryHeapUsed(memoryHeapUsed != null ? memoryHeapUsed.longValue() : null);
        sysActuator.setMemoryG1EdenUsed(memoryG1EdenUsed != null ? memoryG1EdenUsed.longValue() : null);
        sysActuator.setMemoryG1SurvivorUsed(memoryG1SurvivorUsed != null ? memoryG1SurvivorUsed.longValue() : null);
        sysActuator.setMemoryG1OldUsed(memoryG1OldUsed != null ? memoryG1OldUsed.longValue() : null);
        sysActuator.setMemoryCodeCacheMax(memoryCodeCacheMax != null ? memoryCodeCacheMax.longValue() : null);
        sysActuator.setMemoryCodeCacheUsed(memoryCodeCacheUsed != null ? memoryCodeCacheUsed.longValue() : null);
        sysActuator.setMemoryMetaspaceUsed(memoryMetaspaceUsed != null ? memoryMetaspaceUsed.longValue() : null);
        sysActuator.setMemoryCompressClassSpaceMax(memoryCompressClassSpaceMax != null ? memoryCompressClassSpaceMax.longValue() : null);
        sysActuator.setMemoryCompressClassSpaceUsed(memoryCompressClassSpaceUsed != null ? memoryCompressClassSpaceUsed.longValue() : null);
        sysActuator.setDiskFree(diskFree != null ? diskFree.longValue() : null);
        sysActuator.setDiskTotal(diskTotal != null ? diskTotal.longValue() : null);
        sysActuator.setUptime(uptime != null ? uptime.longValue() : null);

        // 非堆内存计算（非堆内存 = Metaspace + CodeCache）
        // 非堆内存最大 = Metaspace max + CodeCache max
        Long calculatedNoheapMax = null;
        if (memoryMetaspaceMax != null && memoryCodeCacheMax != null) {
            calculatedNoheapMax = memoryMetaspaceMax.longValue() + memoryCodeCacheMax.longValue();
        } else if (memoryMetaspaceMax != null) {
            calculatedNoheapMax = memoryMetaspaceMax.longValue();
        } else if (memoryCodeCacheMax != null) {
            calculatedNoheapMax = memoryCodeCacheMax.longValue();
        }

        // 非堆内存使用 = Metaspace used + CodeCache used
        Long calculatedNoheapUsed = null;
        if (memoryMetaspaceUsed != null && memoryCodeCacheUsed != null) {
            calculatedNoheapUsed = memoryMetaspaceUsed.longValue() + memoryCodeCacheUsed.longValue();
        } else if (memoryMetaspaceUsed != null) {
            calculatedNoheapUsed = memoryMetaspaceUsed.longValue();
        } else if (memoryCodeCacheUsed != null) {
            calculatedNoheapUsed = memoryCodeCacheUsed.longValue();
        }

        // 总内存使用 = 堆已使用 + 非堆已使用
        Long totalMemoryUsed = null;
        if (memoryHeapUsed != null && calculatedNoheapUsed != null) {
            totalMemoryUsed = memoryHeapUsed.longValue() + calculatedNoheapUsed;
        } else if (memoryHeapUsed != null) {
            totalMemoryUsed = memoryHeapUsed.longValue();
        } else if (calculatedNoheapUsed != null) {
            totalMemoryUsed = calculatedNoheapUsed;
        }

        // 总内存最大 = 堆最大 + 非堆最大
        Long totalMemoryMax = null;
        if (memoryHeapMax != null && calculatedNoheapMax != null) {
            totalMemoryMax = memoryHeapMax.longValue() + calculatedNoheapMax;
        } else if (memoryHeapMax != null) {
            totalMemoryMax = memoryHeapMax.longValue();
        } else if (calculatedNoheapMax != null) {
            totalMemoryMax = calculatedNoheapMax;
        }

        sysActuator.setMemoryNoheapMax(calculatedNoheapMax);
        sysActuator.setMemoryNoheapUsed(calculatedNoheapUsed);
        sysActuator.setMemoryMetaspaceMax(memoryMetaspaceMax != null ? memoryMetaspaceMax.longValue() : null);
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