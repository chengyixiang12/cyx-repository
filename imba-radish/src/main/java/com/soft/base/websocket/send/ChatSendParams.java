package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/28 17:41
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatSendParams extends AbstractSendParams {

    /**
     * 回答
     */
    private String answer;
}
