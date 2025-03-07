package com.soft.base.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.service.SysUsersService;
import com.soft.base.utils.UniversalUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/15 19:20
 **/

@Slf4j
@Component
public class CaptchaConsume {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UniversalUtil universalUtil;

    private final SysUsersService sysUsersService;

    private final JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String fromEmail;

    @Value(value = "${manage-system.captcha.topic}")
    private String topic;

    @Value(value = "${manage-system.captcha.expire-time}")
    private Long expireTime;

    @Autowired
    public CaptchaConsume(RedisTemplate<String, Object> redisTemplate,
                          UniversalUtil universalUtil,
                          JavaMailSender javaMailSender,
                          SysUsersService sysUsersService) {
        this.redisTemplate = redisTemplate;
        this.universalUtil = universalUtil;
        this.javaMailSender = javaMailSender;
        this.sysUsersService = sysUsersService;
    }

    @RabbitListener(queues = RabbitmqConstant.TOPIC_QUEUE_SEND_LOGIN_CAPTCHA, ackMode = "MANUAL")
    public void sendLoginCaptcha(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("start consume message...");
            String username = new String(message.getBody());

            String email = sysUsersService.getEmail(username);
            String captChat = universalUtil.generate(BaseConstant.LOGIN_CAPTCHA_LENGTH);
            redisTemplate.opsForValue().set(RedisConstant.EMAIL_CAPTCHA_KEY + username, captChat, expireTime, TimeUnit.SECONDS);

            sendEmail(email, captChat);
            channel.basicAck(deliveryTag, false);
        } catch (MessagingException | IOException e) {
            log.error(e.getMessage(), e);
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                log.error(ex.getMessage(), e);
            }

            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitmqConstant.TOPIC_QUEUE_SEND_REGIST_CAPTCHA, ackMode = "MANUAL")
    public void sendRegistCaptcha(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("start consume message...");
            String email = new String(message.getBody());
            String captChat = universalUtil.generate(BaseConstant.LOGIN_CAPTCHA_LENGTH);
            redisTemplate.opsForValue().set(RedisConstant.EMAIL_CAPTCHA_KEY + email, captChat, expireTime, TimeUnit.SECONDS);

            sendEmail(email, captChat);
            channel.basicAck(deliveryTag, false);
        } catch (MessagingException | IOException e) {
            log.error(e.getMessage(), e);
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                log.error(ex.getMessage(), e);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送邮件
     * @param email
     * @param captChat
     * @throws MessagingException
     */
    private void sendEmail(String email, String captChat) throws MessagingException, IOException {
        StringBuilder captchaInfo = new StringBuilder();
        ClassPathResource resource = new ClassPathResource(BaseConstant.EMAIL_CONTENT_PATH);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            captchaInfo.append(reader.readLine());
            captchaInfo.append(captChat);
            captchaInfo.append(reader.readLine());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(topic);
            helper.setText(captchaInfo.toString(), true);
            javaMailSender.send(mimeMessage);
        }
    }
}
