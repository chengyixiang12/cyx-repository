package com.soft.base.schedule;

import com.soft.base.utils.LocalCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/7 10:54
 **/

@Component
@Slf4j
public class LocalCacheStorageTimer {

    private final LocalCacheUtil localCacheUtil;

    @Autowired
    public LocalCacheStorageTimer(LocalCacheUtil localCacheUtil) {
        this.localCacheUtil = localCacheUtil;
    }

    /**
     * 每天1点清理本地缓存
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void clearExpireLocalCache() {
        log.info("localCache clear...");
        long currentTime = System.currentTimeMillis();
        localCacheUtil.getAllExpire().forEach((k,v) -> {
            if (v > currentTime) {
                localCacheUtil.remove(k);
            }
        });
    }
}
