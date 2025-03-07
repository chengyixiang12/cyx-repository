package com.soft.base.websocket.receive;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 指令接收参数
 * @DateTime: 2024/11/23 21:34
 **/
@Data
public class OrderReceiveParams {

    /**
     * 指令
     */
    @NotNull
    private String order;
}
