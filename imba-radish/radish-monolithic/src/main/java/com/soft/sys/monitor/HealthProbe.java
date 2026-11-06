package com.soft.sys.monitor;

import com.soft.sys.entity.SysComponentHealthLog;
import com.soft.sys.entity.SysComponentStatus;
import com.soft.sys.mapper.SysComponentHealthLogMapper;
import com.soft.sys.mapper.SysComponentStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.HealthContributor;
import org.springframework.boot.health.registry.HealthContributorRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyx
 * @description 组件健康探测：每 60 秒一次
 * <p>
 * 与旧实现最大的区别是“未注册的检查项”不再伪造成 UNKNOWN（页面上会显示成红色异常），
 * 而是明确标成 DISABLED——SSL 在 http 环境、邮件在禁用状态下都属于这种情况。
 * 状态只在变化时追加一条记录，正常时的重复探测不写历史。
 * @date 2026-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthProbe {

    /**
     * 需要探测的组件；不包含 ping（永远 UP，没有信息量）
     */
    private static final List<ComponentDef> COMPONENTS = List.of(
            new ComponentDef("db", "数据库"),
            new ComponentDef("redis", "Redis"),
            new ComponentDef("diskSpace", "磁盘空间"),
            new ComponentDef("ssl", "SSL 证书")
    );

    private final HealthEndpoint healthEndpoint;
    private final ObjectProvider<HealthContributorRegistry> contributorRegistry;
    private final SysComponentStatusMapper statusMapper;
    private final SysComponentHealthLogMapper healthLogMapper;

    /**
     * 上一次探测到的状态，用于判断是否需要写变更记录
     */
    private final Map<String, String> lastStatus = new HashMap<>();

    @Scheduled(fixedRate = 60_000, initialDelay = 20_000)
    public void probe() {
        LocalDateTime now = LocalDateTime.now();
        HealthContributorRegistry registry = contributorRegistry.getIfAvailable();
        if (lastStatus.isEmpty()) {
            loadLastStatus();
        }

        for (ComponentDef def : COMPONENTS) {
            String status;
            String detail;

            HealthContributor contributor = registry == null ? null : registry.getContributor(def.key());
            if (contributor == null) {
                status = Status.DISABLED;
                detail = "未启用该健康检查";
            } else {
                try {
                    HealthDescriptor descriptor = healthEndpoint.healthForPath(def.key());
                    status = descriptor == null ? Status.UNKNOWN : descriptor.getStatus().getCode();
                    detail = Status.UP.equals(status) ? null : def.name() + "返回 " + status;
                } catch (Exception e) {
                    status = Status.UNKNOWN;
                    detail = "探测失败：" + e.getMessage();
                }
            }

            try {
                statusMapper.upsert(def.key(), def.name(), status, detail, now);
                recordIfChanged(def, status, detail, now);
            } catch (Exception e) {
                log.error("更新组件状态失败：{}", def.key(), e);
            }
        }
    }

    private void recordIfChanged(ComponentDef def, String status, String detail, LocalDateTime now) {
        String previous = lastStatus.put(def.key(), status);
        if (status.equals(previous)) {
            return;
        }
        SysComponentHealthLog change = new SysComponentHealthLog();
        change.setComponent(def.key());
        change.setComponentName(def.name());
        change.setStatus(status);
        change.setDetail(detail);
        change.setChangeTime(now);
        healthLogMapper.insert(change);
        log.warn("组件状态变化：{} {} -> {}", def.name(), previous, status);
    }

    private void loadLastStatus() {
        for (SysComponentStatus status : statusMapper.selectAllOrdered()) {
            lastStatus.put(status.getComponent(), status.getStatus());
        }
    }

    private record ComponentDef(String key, String name) {
    }

    /**
     * 组件状态枚举；DISABLED 表示该检查项未注册（未启用），不是故障
     */
    private static final class Status {

        private static final String UP = "UP";
        private static final String DISABLED = "DISABLED";
        private static final String UNKNOWN = "UNKNOWN";

        private Status() {
        }
    }
}
