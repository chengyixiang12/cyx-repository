package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyq
 * @date 2025/11/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RefreshTokenSendParam extends AbstractSendParams {

    /**
     * 刷新后的token
     */
    private String token;
}
