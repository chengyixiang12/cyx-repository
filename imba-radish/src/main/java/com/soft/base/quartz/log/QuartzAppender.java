package com.soft.base.quartz.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import lombok.Setter;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author cyq
 * @date 2025/11/11
 * @description
 */
@Setter
public class QuartzAppender extends AppenderBase<ILoggingEvent> {

    // 用于存储收集到的日志（每个线程独立）
    private static final ThreadLocal<StringBuilder> logCollector = new ThreadLocal<>();

    // MDC 中的标记键名
    private static final String COLLECT_FLAG_KEY = "job.log.collect";

    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        // 关键：检查该日志事件的 MDC 中是否包含收集标记
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap != null && "1".equals(mdcMap.get(COLLECT_FLAG_KEY))) {
            StringBuilder sb = logCollector.get();
            if (sb != null && layout != null) {
                sb.append(layout.doLayout(event));
            }
        }
    }

    // ---------- 供外部调用的方法 ----------
    public static void startCollect() {
        MDC.put(COLLECT_FLAG_KEY, "1");  // 设置标记
        logCollector.set(new StringBuilder());  // 初始化收集器
    }

    public static String stopAndGet() {
        MDC.remove(COLLECT_FLAG_KEY);  // 移除标记
        StringBuilder sb = logCollector.get();
        logCollector.remove();  // 清理 ThreadLocal
        return sb == null ? "" : sb.toString();
    }
}
