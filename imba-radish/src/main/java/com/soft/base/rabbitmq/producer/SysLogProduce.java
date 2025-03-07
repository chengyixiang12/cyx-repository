package com.soft.base.rabbitmq.producer;

import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.dto.LogDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 13:59
 **/

@Component
public class SysLogProduce {

    private final RabbitTemplate rabbitTemplate;

    public final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public SysLogProduce(RabbitTemplate rabbitTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 推送日志
     * @param logDto
     */
    public void sendSysLog(LogDto logDto) {
        String key = RedisConstant.SYS_LOG_CACHE + System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, logDto);
        rabbitTemplate.convertAndSend(RabbitmqConstant.DIRECT_EXCHANGE, RabbitmqConstant.DIRECT_ROUTEKEY_ONE, key);
    }
}
