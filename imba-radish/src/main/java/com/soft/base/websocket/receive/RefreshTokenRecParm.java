package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyq
 * @date 2025/11/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RefreshTokenRecParm extends AbstractRecParam {

    /**
     * 客户端指纹
     */
    private String fingerprint;
}
