package com.soft.sys.model.dto.rabbitmq;

import lombok.Data;

/**
 * MQ 消息体需保证有无参构造器，否则 Jackson 反序列化会失败。
 *
 * @author cyq
 * @date 2025/12/20
 * @description
 */
@Data
public class EmailDTO {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 消息内容
     */
    private String content;
}
