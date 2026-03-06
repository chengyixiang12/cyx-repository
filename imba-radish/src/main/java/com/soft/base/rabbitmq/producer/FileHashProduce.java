package com.soft.base.rabbitmq.producer;

import com.soft.base.constants.RabbitmqConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2026/3/6
 * @description
 */
@Component
@RequiredArgsConstructor
public class FileHashProduce {

    private final RabbitTemplate rabbitTemplate;

    public void send(Long fileId) {
        rabbitTemplate.convertAndSend(RabbitmqConstant.TOPIC_EXCHANGE, RabbitmqConstant.TOPIC_ROUTE_KEY_FILEHASH, fileId);
    }
}
