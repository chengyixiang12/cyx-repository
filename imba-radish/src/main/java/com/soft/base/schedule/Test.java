package com.soft.base.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.soft.base.utils.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author cyq
 * @date 2026/3/26
 * @description
 */

@Component
@Slf4j
public class Test {

    private final HttpUtil httpUtil;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public Test(HttpUtil httpUtil, @Qualifier("radishTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.httpUtil = httpUtil;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

//    @Scheduled(initialDelay = 1000, fixedDelay = 10000)
//    public void test() {
//        Map<String, String> header = new HashMap<>();
//        header.put("Authorization", "Bearer eeb22c93-6f00-4cee-a874-5cc5afee8182");
//        header.put("Content-Type", "application/json");
//        header.put("origin", "http://imba-radish.top");
//        header.put("Accept", "application/json");
//        header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0");
//        CompletableFuture.runAsync(() -> {
//            JSONObject jsonObject = httpUtil.syncMonoPost("http://imba-radish.top", "/api/permission/getPermissions", new HashMap<>(), header, new HashMap<>(), JSONObject.class);
//            log.info(jsonObject.toString());
//        }, threadPoolTaskExecutor);
//    }
}
