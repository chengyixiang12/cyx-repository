package com.soft.base.websocket.receive;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 指令接收参数
 * @DateTime: 2024/11/23 21:34
 **/
@Data
public abstract class AbstractReceiveParams {

    /**
     * 指令
     */
    private String order;

    /**
     * 消息
     */
    private String msg;

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
