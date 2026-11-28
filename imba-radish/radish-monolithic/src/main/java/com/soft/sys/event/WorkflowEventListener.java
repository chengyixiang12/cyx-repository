package com.soft.sys.event;

import com.soft.sys.entity.SysUser;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.model.dto.WorkflowEventDTO;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.service.SysUsersService;
import com.soft.sys.websocket.send.WorkflowNotifyResponse;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 工作流通知监听器：流程事件 → WebSocket 推送 + 邮件通知，异步消费。
 *
 * @author cyx
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEventListener {

    private final SysUsersService sysUsersService;

    private final EmailPublisher emailPublisher;

    private final RadishProperty radishProperty;

    /**
     * 工作流事件通知
     */
    @Async
    @EventListener
    public void onWorkflowEvent(WorkflowEventDTO event) {
        try {
            pushWebSocket(event);
        } catch (Exception e) {
            log.error("工作流WebSocket推送失败, receiverId={}", event.getReceiverId(), e);
        }
        try {
            sendEmail(event);
        } catch (Exception e) {
            log.error("工作流邮件发送失败, receiverId={}", event.getReceiverId(), e);
        }
    }

    /**
     * 给接收人所有在线设备推送工作流通知（不依赖会话请求）
     */
    private void pushWebSocket(WorkflowEventDTO event) throws IOException {
        List<WebSocketSession> sessions = WebSocketSessionManager.getSessions(event.getReceiverId());
        if (sessions.isEmpty()) {
            log.debug("用户 {} 不在线，跳过WebSocket推送", event.getReceiverId());
            return;
        }
        WorkflowNotifyResponse response = new WorkflowNotifyResponse();
        response.setStatus(true);
        response.setOrder(WebSocketOrderEnum.WORKFLOW_NOTIFY.toString());
        response.setMsg(event.getTitle());
        response.setDetail(event.getDetail());
        Optional.ofNullable(event.getInstanceId()).ifPresent(id -> response.setInstanceId(String.valueOf(id)));
        Optional.ofNullable(event.getTaskId()).ifPresent(id -> response.setTaskId(String.valueOf(id)));
        String payload = response.toJsonString();
        for (WebSocketSession session : sessions) {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(payload));
            }
        }
    }

    /**
     * 发送工作流通知邮件
     */
    private void sendEmail(WorkflowEventDTO event) {
        SysUser receiver = sysUsersService.getById(event.getReceiverId());
        if (receiver == null || StringUtils.isBlank(receiver.getEmail())) {
            log.debug("接收人 {} 不存在或未配置邮箱，跳过邮件通知", event.getReceiverId());
            return;
        }
        String topic = Optional.ofNullable(radishProperty.getWorkflow())
                .map(RadishProperty.Workflow::getTopic)
                .filter(StringUtils::isNotBlank)
                .orElse("工作流通知");
        String content = "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>工作流通知</title><style>body {background-color: #f9f9f9;margin: 0;padding: 0;font-family: Arial, sans-serif;color: #333;}.email-container {max-width: 600px;margin: 50px auto;background-color: #ffffff;padding: 20px;border-radius: 8px;box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);text-align: center;}.email-header {font-size: 18px;font-weight: bold;margin-bottom: 20px;}.email-content p {font-size: 16px;line-height: 1.6;margin: 10px 0;} .email-footer{font-size: 12px;color: #999;margin-top: 20px;}</style></head><body><div class=\"email-container\"><div class=\"email-header\">"
                + event.getTitle()
                + "</div><div class=\"email-content\"><p>尊敬的用户您好！</p><p>"
                + event.getDetail()
                + "</p><p>请登录系统查看详情。</p><p>感谢您的使用！</p></div><div class=\"email-footer\">此邮件由系统自动发送，请勿回复。</div></div></body></html>";
        emailPublisher.send(receiver.getEmail(), topic, content);
    }
}
