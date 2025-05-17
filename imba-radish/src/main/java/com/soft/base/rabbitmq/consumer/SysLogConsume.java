package com.soft.base.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.model.dto.LogDto;
import com.soft.base.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 14:11
 **/

@Component
@Slf4j
public class SysLogConsume {

    private final RedisTemplate<String,Object> redisTemplate;

    private final SysLogService sysLogService;

    @Autowired
    public SysLogConsume(RedisTemplate<String,Object> redisTemplate, SysLogService sysLogService) {
        this.redisTemplate = redisTemplate;
        this.sysLogService = sysLogService;
    }

    /**
     * 保存日志
     * @param message
     * @param channel
     */
    @RabbitListener(queues = RabbitmqConstant.DIRECT_QUEUE_ONE, ackMode = "MANUAL")
    public void saveSysLog(Message message, Channel channel) {
        String key = null;
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            key = new String(message.getBody());

            LogDto logDto = (LogDto) redisTemplate.opsForValue().get(key);
            sysLogService.saveLog(logDto);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                log.error(ex.getMessage(), e);
            }
            throw new RuntimeException(e);
        } finally {
            if (key != null) {
                redisTemplate.delete(key);
            }
        }
    }
}
