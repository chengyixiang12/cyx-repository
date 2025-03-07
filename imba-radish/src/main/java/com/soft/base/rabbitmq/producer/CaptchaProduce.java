package com.soft.base.rabbitmq.producer;

import com.soft.base.constants.RabbitmqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/16 20:06
 **/

@Component
public class CaptchaProduce {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CaptchaProduce(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送登录验证码
     * @param username
     */
    public void sendLoginCaptcha(String username) {
        rabbitTemplate.convertAndSend(RabbitmqConstant.TOPIC_EXCHANGE, RabbitmqConstant.TOPIC_ROUTE_KEY_LOGIN, username);
    }

    /**
     * 发送注册验证码
     * @param email
     */
    public void sendRegistCaptcha(String email) {
        rabbitTemplate.convertAndSend(RabbitmqConstant.TOPIC_EXCHANGE, RabbitmqConstant.TOPIC_ROUTE_KEY_REGIST, email);
    }
}
