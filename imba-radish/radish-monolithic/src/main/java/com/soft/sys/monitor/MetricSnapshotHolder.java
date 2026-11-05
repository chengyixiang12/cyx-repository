package com.soft.sys.monitor;

import com.soft.sys.model.vo.MetricOverviewVO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 最新一次采样的内存快照
 * <p>
 * “当前值”这类数据不需要查库：采集器每 30 秒刷新一次，接口直接读内存。
 * 这样总览接口的耗时与数据库无关，也不会因为库慢而卡住页面。
 * @date 2026-11-05
 */
@Component
public class MetricSnapshotHolder {

    private volatile MetricOverviewVO overview;

    public MetricOverviewVO current() {
        MetricOverviewVO snapshot = this.overview;
        if (snapshot == null) {
            // 应用刚启动、采集器还没跑第一轮时的兜底，避免前端拿到 null
            snapshot = new MetricOverviewVO();
            snapshot.setSampleTime(LocalDateTime.now());
        }
        return snapshot;
    }

    public void update(MetricOverviewVO overview) {
        this.overview = overview;
    }
}
