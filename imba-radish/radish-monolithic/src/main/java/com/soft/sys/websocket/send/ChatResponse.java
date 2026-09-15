package com.soft.sys.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/28 17:41
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatResponse extends AbstractWebSocketResponse {

    /**
     * 回答
     */
    private String answer;
}
