package com.soft.base.websocket.send;

import lombok.Data;

/**
 * @Author: cyx
 * @Description: 发送消息抽象类
 * @DateTime: 2024/11/23 21:36
 **/

@Data
public class SendParams {

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 返回信息
     */
    private String message;
}
