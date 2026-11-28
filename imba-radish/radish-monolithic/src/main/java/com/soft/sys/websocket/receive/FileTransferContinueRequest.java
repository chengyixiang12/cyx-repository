package com.soft.sys.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/1/4 10:03
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTransferContinueRequest extends AbstractWebSocketRequest {

    /**
     * 文件hash值
     */
    private String fileHash;
}
