package com.soft.sys.event;

import com.soft.sys.model.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 发布邮件发送事件，由 {@link MessageEventListener} 异步消费。
 *
 * @author cyx
 */
@Component
@RequiredArgsConstructor
public class EmailPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发送邮件
     *
     * @param email   收件邮箱
     * @param content 邮件内容
     */
    public void send(String email, String content) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(email);
        emailDTO.setContent(content);
        eventPublisher.publishEvent(emailDTO);
    }

    /**
     * 发送带自定义主题的邮件
     *
     * @param email   收件邮箱
     * @param subject 邮件主题；为空时回退到默认主题
     * @param content 邮件内容
     */
    public void send(String email, String subject, String content) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(email);
        emailDTO.setSubject(subject);
        emailDTO.setContent(content);
        eventPublisher.publishEvent(emailDTO);
    }
}
