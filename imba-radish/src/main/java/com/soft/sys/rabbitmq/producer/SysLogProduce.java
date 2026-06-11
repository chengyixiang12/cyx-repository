package com.soft.sys.rabbitmq.producer;

import com.soft.sys.constants.RabbitmqConstant;
import com.soft.sys.model.dto.LogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description:
 * @DateTime: 2024/11/21 13:59
 **/

@Component
@RequiredArgsConstructor
public class SysLogProduce {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 推送日志
     * @param logDto
     */
    public void send(LogDto logDto) {
        rabbitTemplate.convertAndSend(RabbitmqConstant.Direct.EXCHANGE, RabbitmqConstant.Direct.ROUTE_KEY_ONE, logDto);
    }
}
