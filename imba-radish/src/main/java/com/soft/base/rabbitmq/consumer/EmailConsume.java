package com.soft.base.rabbitmq.consumer;

import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.dto.rabbitmq.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/15 19:20
 **/

@Slf4j
@Component
public class EmailConsume {

    private final JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String fromEmail;

    @Value(value = "${radish.captcha.topic}")
    private String topic;

    @Autowired
    public EmailConsume(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitmqConstant.TOPIC_QUEUE_SEND_EMAIL)
    public void sendLoginCaptcha(EmailDto emailDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(emailDto.getEmail());
            helper.setSubject(topic);
            helper.setText(emailDto.getContent(), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new GlobalException(e);
        }
    }
}
