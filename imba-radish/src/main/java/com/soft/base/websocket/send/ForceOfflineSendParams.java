package com.soft.base.websocket.send;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 强制离线发送消息参数
 * @DateTime: 2024/11/23 22:17
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ForceOfflineSendParams extends SendParams {

    /**
     * 指令
     */
    @NotNull
    private String order;
}
