package com.soft.sys.event;

import com.soft.sys.model.dto.EmailDTO;
import com.soft.sys.model.dto.LogDTO;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.service.SysLogService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 异步消息监听器，替代原 RabbitMQ 消费者。
 *
 * @author cyx
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener {

    private final MailProperties mailProperties;

    private final RadishProperty radishProperty;

    private final JavaMailSender javaMailSender;

    private final SysLogService sysLogService;

    /**
     * 发送验证码邮件
     */
    @Async
    @EventListener
    public void sendCaptcha(EmailDTO emailDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(Optional.ofNullable(mailProperties.getUsername()).orElseThrow());
            helper.setTo(emailDto.getEmail());
            helper.setSubject(radishProperty.getCaptcha().getTopic());
            helper.setText(emailDto.getContent(), true);
            javaMailSender.send(mimeMessage);
            log.info("邮件发送成功");
        } catch (Exception e) {
            log.warn("邮件发送失败");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 保存系统日志
     */
    @Async
    @EventListener
    public void saveSysLog(LogDTO logDto) {
        try {
            sysLogService.saveLog(logDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
