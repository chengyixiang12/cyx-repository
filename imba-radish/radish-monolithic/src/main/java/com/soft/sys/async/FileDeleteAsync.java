package com.soft.sys.async;

import com.soft.sys.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: 文件对象异步清理
 * <p>
 * 数据库记录被物理删除后，若同一个 objectKey 已无任何记录引用，则异步删除 MinIO 中的对象。
 * MinIO 的 removeObject 本身是幂等的（对象不存在也会返回成功），因此重试是安全的。
 * @DateTime: 2026/11/05
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class FileDeleteAsync {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY = 3;

    private final MinioUtil minioUtil;

    @Async
    public void deleteObject(String bucket, String objectKey) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                minioUtil.delete(bucket, objectKey);
                log.debug("MinIO 对象删除成功：bucket={}, objectKey={}", bucket, objectKey);
                return;
            } catch (Exception e) {
                log.warn("MinIO 对象删除失败（第 {} 次）：bucket={}, objectKey={}", attempt, bucket, objectKey, e);
                if (attempt == MAX_RETRY) {
                    log.error("MinIO 对象删除最终失败，该对象可能永久残留，需人工清理：bucket={}, objectKey={}", bucket, objectKey);
                    return;
                }
                try {
                    Thread.sleep(200L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
