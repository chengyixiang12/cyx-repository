package com.soft.base.rabbitmq.consumer;

import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.model.dto.LogDto;
import com.soft.base.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 14:11
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class SysLogConsume {

    private final SysLogService sysLogService;

    /**
     * 保存日志
     * @param logDto
     */
    @RabbitListener(queues = RabbitmqConstant.DIRECT_QUEUE_ONE)
    public void saveSysLog(LogDto logDto) {
        sysLogService.saveLog(logDto);
    }
}
