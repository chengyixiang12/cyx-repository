package com.soft.sys.event;

import com.soft.sys.model.dto.WorkflowEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 发布工作流通知事件，由 {@code WorkflowEventListener} 异步消费。
 *
 * @author cyx
 */
@Component
@RequiredArgsConstructor
public class WorkflowEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发送工作流通知
     *
     * @param workflowEventDTO 通知载荷
     */
    public void send(WorkflowEventDTO workflowEventDTO) {
        eventPublisher.publishEvent(workflowEventDTO);
    }
}
