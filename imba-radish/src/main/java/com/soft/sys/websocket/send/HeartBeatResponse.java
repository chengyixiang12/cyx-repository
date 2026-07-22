package com.soft.sys.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyq
 * @date 2025/11/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HeartBeatResponse extends AbstractWebSocketResponse {

    /**
     * 是否刷新token
     */
    private boolean refreshFlag = false;

}
