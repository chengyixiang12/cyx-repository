package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 强制离线接收参数
 * @DateTime: 2024/11/23 21:56
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ForceOfflineRecParam extends AbstractRecParam {

    /**
     * 接收者
     */
    private Long receiver;
}
