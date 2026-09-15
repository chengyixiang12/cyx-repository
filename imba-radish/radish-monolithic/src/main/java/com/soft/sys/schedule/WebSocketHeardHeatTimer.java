package com.soft.sys.schedule;

import com.soft.sys.async.SendPingAsync;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 心跳
 * @author cyx
 * @date 2026-06-18
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHeardHeatTimer {

    private final SendPingAsync sendPingAsync;

    @Scheduled(cron = "*/15 * * * * *")
    public void heartBeat() {
        Set<String> keys = WebSocketSessionManager.getKeys();
        if (keys.isEmpty()) return;
        keys.forEach(sendPingAsync::send);
    }
}
