package com.soft.sys.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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

import java.util.Date;

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

    @Scheduled(cron = "*/15 * * * * *")
    public void run() {
        SysActuator sysActuator = new SysActuator();

        // ========== 1. 基础系统指标 ==========
        Double cpuUsage = getGaugeValue("system.cpu.usage");
        Double cpuCount = getGaugeValue("system.cpu.count");
        Double uptime = getGaugeValue("process.uptime");

        // ========== 2. 磁盘指标 ==========
        Double diskFree = getGaugeValue("disk.free");
        Double diskTotal = getGaugeValue("disk.total");

        // ========== 3. 堆内存指标（G1收集器） ==========
        Double memoryHeapMax = getGaugeValue("jvm.memory.max", Tags.of("area", "heap"));
        
        // G1各区域使用量
        Double memoryG1EdenUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Eden Space"));
        Double memoryG1SurvivorUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Survivor Space"));
        Double memoryG1OldUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "G1 Old Gen"));
        
        // 堆内存使用量 = Eden + Survivor + Old
        Double memoryHeapUsed = sumNonNull(memoryG1EdenUsed, memoryG1SurvivorUsed, memoryG1OldUsed);

        // ========== 4. 非堆内存指标 ==========
        Double memoryMetaspaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Metaspace"));
        Double memoryMetaspaceMax = getGaugeValue("jvm.memory.max", Tags.of("id", "Metaspace"));
        
        Double memoryCodeCacheUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "CodeCache"));
        Double memoryCodeCacheMax = getGaugeValue("jvm.memory.max", Tags.of("id", "CodeCache"));
        
        Double memoryCompressClassSpaceUsed = getGaugeValue("jvm.memory.used", Tags.of("id", "Compressed Class Space"));
        Double memoryCompressClassSpaceMax = getGaugeValue("jvm.memory.max", Tags.of("id", "Compressed Class Space"));

        // ========== 5. 计算聚合指标 ==========
        // 非堆内存最大 = Metaspace max + CodeCache max
        Long calculatedNoheapMax = sumNonNullToLong(memoryMetaspaceMax, memoryCodeCacheMax);
        // 非堆内存使用 = Metaspace used + CodeCache used
        Long calculatedNoheapUsed = sumNonNullToLong(memoryMetaspaceUsed, memoryCodeCacheUsed);
        // 总内存使用 = 堆已使用 + 非堆已使用
        Long totalMemoryUsed = sumNonNullToLong(memoryHeapUsed) + calculatedNoheapUsed;
        // 总内存最大 = 堆最大 + 非堆最大
        Long totalMemoryMax = sumNonNullToLong(memoryHeapMax) + calculatedNoheapMax;

        // ========== 6. 设置实体属性 ==========
        // CPU和运行时间
        sysActuator.setCpuUsage(cpuUsage);
        sysActuator.setCpuCount(toInt(cpuCount, 0));
        sysActuator.setUptime(toLong(uptime, 0L));

        // 磁盘
        sysActuator.setDiskFree(toLong(diskFree, 0L));
        sysActuator.setDiskTotal(toLong(diskTotal, 0L));

        // 堆内存
        sysActuator.setMemoryHeapMax(toLong(memoryHeapMax, 0L));
        sysActuator.setMemoryHeapUsed(toLong(memoryHeapUsed, 0L));
        sysActuator.setMemoryG1EdenUsed(toLong(memoryG1EdenUsed, 0L));
        sysActuator.setMemoryG1SurvivorUsed(toLong(memoryG1SurvivorUsed, 0L));
        sysActuator.setMemoryG1OldUsed(toLong(memoryG1OldUsed, 0L));

        // 非堆内存
        sysActuator.setMemoryMetaspaceUsed(toLong(memoryMetaspaceUsed, 0L));
        sysActuator.setMemoryMetaspaceMax(toLong(memoryMetaspaceMax, 0L));
        sysActuator.setMemoryCodeCacheUsed(toLong(memoryCodeCacheUsed, 0L));
        sysActuator.setMemoryCodeCacheMax(toLong(memoryCodeCacheMax, 0L));
        sysActuator.setMemoryCompressClassSpaceUsed(toLong(memoryCompressClassSpaceUsed, 0L));
        sysActuator.setMemoryCompressClassSpaceMax(toLong(memoryCompressClassSpaceMax, 0L));

        // 聚合内存指标
        sysActuator.setMemoryNoheapMax(calculatedNoheapMax);
        sysActuator.setMemoryNoheapUsed(calculatedNoheapUsed);
        sysActuator.setMemoryUsed(totalMemoryUsed);
        sysActuator.setMemoryMax(totalMemoryMax);

        // ========== 7. 健康状态 ==========
        HealthComponent health = healthEndpoint.health();
        sysActuator.setHealth(health.getStatus().getCode());
        
        sysActuator.setHealthDb(getComponentStatus("db"));
        sysActuator.setHealthRedis(getComponentStatus("redis"));
        sysActuator.setHealthRabbit(getComponentStatus("rabbit"));
        sysActuator.setHealthSsl(getComponentStatus("ssl"));
        sysActuator.setHealthDiskSpace(getComponentStatus("diskSpace"));
        sysActuator.setHealthPing(getComponentStatus("ping"));

        // 存入数据库
        sysActuatorService.save(sysActuator);
    }

    /**
     * 每天0点清理一个月之前的监控记录
     */
    @Scheduled(cron = "* * 0 * * *")
    public void clear() {
        DateTime dateTime = DateUtil.offsetMonth(new Date(), -1);
        sysActuatorService.deleteOneMonthAgo(dateTime);
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

    /**
     * 求和非空的Double值
     */
    private Double sumNonNull(Double... values) {
        double sum = 0;
        boolean hasNonNull = false;
        for (Double value : values) {
            if (value != null) {
                sum += value;
                hasNonNull = true;
            }
        }
        return hasNonNull ? sum : null;
    }

    /**
     * 求和非空的Double值并转换为Long
     */
    private Long sumNonNullToLong(Double... values) {
        long sum = 0;
        for (Double value : values) {
            if (value != null) {
                sum += value.longValue();
            }
        }
        return sum;
    }

    /**
     * Double转Integer，空值返回默认值
     */
    private Integer toInt(Double value, int defaultValue) {
        return value != null ? value.intValue() : defaultValue;
    }

    /**
     * Double转Long，空值返回默认值
     */
    private Long toLong(Double value, Long defaultValue) {
        return value != null ? value.longValue() : defaultValue;
    }
}
