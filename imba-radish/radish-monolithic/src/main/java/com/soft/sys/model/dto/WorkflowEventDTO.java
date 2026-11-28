package com.soft.sys.model.dto;

import lombok.Data;

/**
 * 工作流通知事件载荷，由 {@code WorkflowEventListener} 异步消费
 * （WebSocket 推送 + 邮件通知）。
 *
 * @author cyx
 * @date 2026/11/19
 * @description
 */
@Data
public class WorkflowEventDTO {

    /**
     * 通知接收人id
     */
    private Long receiverId;

    /**
     * 通知标题（WebSocket 消息 msg 与邮件主题）
     */
    private String title;

    /**
     * 通知详情（邮件正文摘要）
     */
    private String detail;

    /**
     * 流程实例id
     */
    private Long instanceId;

    /**
     * 任务id（有任务上下文时）
     */
    private Long taskId;
}
