package com.soft.base.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.model.dto.LogDto;
import com.soft.base.model.dto.rabbitmq.EmailDto;
import com.soft.base.service.SysLogService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${spring.mail.username}")
    private String fromEmail;

    @Value(value = "${radish.captcha.topic}")
    private String topic;

    private final JavaMailSender javaMailSender;

    private final SysLogService sysLogService;

    /**
     * 发送验证码
     * @param emailDto
     */
    @RabbitListener(queues = RabbitmqConstant.Topic.QUEUE_SEND_EMAIL)
    public void sendCaptcha(EmailDto emailDto, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(emailDto.getEmail());
            helper.setSubject(topic);
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
    public void saveSysLog(LogDto logDto, Channel channel,
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
