package com.soft.base.websocket.receive;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 推送消息接收参数
 * @DateTime: 2024/11/23 23:06
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class PushMessageRecParams extends OrderReceiveParams {

    /**
     * 消息
     */
    private String message;

    /**
     * 接收者
     */
    private JSONArray receivers;
}
