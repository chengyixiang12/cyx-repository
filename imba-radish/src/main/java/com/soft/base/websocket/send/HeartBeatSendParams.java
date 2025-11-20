package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyq
 * @date 2025/11/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HeartBeatSendParams extends AbstractSendParams{
    /**
     * token
     */
    private String token;
}
