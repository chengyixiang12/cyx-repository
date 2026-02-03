package com.soft.base.model.dto.rabbitmq;

import lombok.Builder;
import lombok.Data;

/**
 * @author cyq
 * @date 2025/12/20
 * @description
 */
@Data
@Builder
public class EmailDto {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 消息内容
     */
    private String content;
}
