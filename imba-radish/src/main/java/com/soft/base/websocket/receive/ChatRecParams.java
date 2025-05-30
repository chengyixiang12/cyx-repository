package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/28 17:40
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatRecParams extends OrderReceiveParams {

    /**
     * 问题
     */
    private String question;

    /**
     * 对话id
     */
    private Long dialogueId;
}
