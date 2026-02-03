package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 推送消息发送参数
 * @DateTime: 2024/11/23 23:09
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PushMessageSendParams extends AbstractSendParams {

    /**
     * 消息体
     */
    private String body;

}
