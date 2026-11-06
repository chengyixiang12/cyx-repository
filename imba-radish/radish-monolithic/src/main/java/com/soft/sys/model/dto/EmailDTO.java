package com.soft.sys.model.dto;

import lombok.Data;

/**
 * 邮件发送事件载荷。
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
