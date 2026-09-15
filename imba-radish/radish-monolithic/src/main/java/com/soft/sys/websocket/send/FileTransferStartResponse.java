package com.soft.sys.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/12/30 15:31
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTransferStartResponse extends AbstractWebSocketResponse {

    /**
     * 文件唯一标识
     */
    private String fileKey;
}
