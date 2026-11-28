package com.soft.sys.event;

import com.soft.sys.model.dto.LogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 发布系统日志事件，由 {@link MessageEventListener} 异步落库。
 *
 * @author cyx
 */
@Component
@RequiredArgsConstructor
public class SysLogPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 推送日志
     */
    public void send(LogDTO logDto) {
        eventPublisher.publishEvent(logDto);
    }
}
