package com.soft.sys.schedule;

import com.soft.sys.file.ChunkStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 上传分片残留回收。
 *
 * <p>关闭浏览器 / 断网 / 进程重启时前端不会发出任何通知，服务端无法区分
 * "用户稍后回来续传" 和 "已经放弃"。因此采用 TTL：保留 24 小时让断点续传仍可用，
 * 超过后由本任务递归删除，孤儿分片不会永久堆积在 tmp 里。</p>
 *
 * <p>判断活跃度用的是目录内所有文件的最后修改时间，只要还在传分片就不会被回收。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChunkCleanupJob {

    private final ChunkStorage chunkStorage;

    @Value("${radish.upload.chunk-ttl-hours:24}")
    private long ttlHours;

    @Value("${radish.upload.cleanup-legacy:true}")
    private boolean cleanupLegacy;

    @Scheduled(fixedDelayString = "${radish.upload.cleanup-interval-ms:3600000}",
            initialDelayString = "${radish.upload.cleanup-initial-delay-ms:60000}")
    public void cleanup() {
        try {
            Duration ttl = Duration.ofHours(Math.max(1L, ttlHours));
            int removed = chunkStorage.cleanupExpired(ttl, cleanupLegacy);
            if (removed > 0) {
                log.info("回收过期上传分片目录 {} 个（TTL {}h）", removed, ttlHours);
            }
        } catch (Exception e) {
            // 清理失败不能影响调度线程后续执行
            log.error("回收上传分片失败", e);
        }
    }
}
