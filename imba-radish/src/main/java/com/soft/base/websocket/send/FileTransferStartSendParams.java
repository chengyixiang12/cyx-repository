package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/30 15:31
 **/

@EqualsAndHashCode(callSuper = false)
@Data
public class FileTransferStartSendParams extends SendParams {

    /**
     * 文件唯一标识
     */
    private String fileKey;
}
