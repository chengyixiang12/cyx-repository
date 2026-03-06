package com.soft.base.rabbitmq.consumer;

import com.soft.base.constants.RabbitmqConstant;
import com.soft.base.model.dto.rabbitmq.GenerateFileHashDto;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author cyq
 * @date 2026/3/6
 * @description
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class FileHashConsume {

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;

    @RabbitListener(queues = RabbitmqConstant.TOPIC_QUEUE_SEND_FILEHASH)
    public void generateFileHash(GenerateFileHashDto generateFileHashDto) {

    }
}
