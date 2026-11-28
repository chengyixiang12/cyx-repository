package com.soft.sys.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作流通知推送消息体。
 *
 * @author cyx
 * @date 2026/11/19
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkflowNotifyResponse extends AbstractWebSocketResponse {

    /**
     * 流程实例id
     */
    private String instanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 通知详情
     */
    private String detail;
}
