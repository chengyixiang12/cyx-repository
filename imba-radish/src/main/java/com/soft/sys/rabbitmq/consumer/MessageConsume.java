package com.soft.sys.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.soft.sys.constants.RabbitmqConstant;
import com.soft.sys.model.dto.LogDTO;
import com.soft.sys.model.dto.rabbitmq.EmailDTO;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.service.SysLogService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description:
 * @DateTime: 2024/11/15 19:20
 **/

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsume {

    private final MailProperties mailProperties;

    private final RadishProperty radishProperty;

    private final JavaMailSender javaMailSender;

    private final SysLogService sysLogService;

    /**
     * 发送验证码
     * @param emailDto
     */
    @RabbitListener(queues = RabbitmqConstant.Topic.QUEUE_SEND_EMAIL)
    public void sendCaptcha(EmailDTO emailDto, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(emailDto.getEmail());
            helper.setSubject(radishProperty.getCaptcha().getTopic());
            helper.setText(emailDto.getContent(), true);
            javaMailSender.send(mimeMessage);
            channel.basicAck(deliveryTag, false);
            log.info("邮件发送成功");
        } catch (Exception e) {
            log.warn("邮件发送失败");
            log.error(e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 保存日志
     * @param logDto
     */
    @RabbitListener(queues = RabbitmqConstant.Direct.QUEUE_ONE)
    public void saveSysLog(LogDTO logDto, Channel channel,
                           @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            sysLogService.saveLog(logDto);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
