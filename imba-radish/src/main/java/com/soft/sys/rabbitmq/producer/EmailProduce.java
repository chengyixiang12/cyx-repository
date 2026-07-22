package com.soft.sys.rabbitmq.producer;

import com.soft.sys.constants.RabbitmqConstant;
import com.soft.sys.model.dto.rabbitmq.EmailDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description:
 * @DateTime: 2024/11/16 20:06
 **/

@Component
public class EmailProduce {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmailProduce(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送登录验证码
     * @param email
     */
    public void send(String email, String content) {
        rabbitTemplate.convertAndSend(RabbitmqConstant.Topic.EXCHANGE, RabbitmqConstant.Topic.ROUTE_KEY_EMAIL, EmailDTO.builder().email(email).content(content).build());
    }
}
