package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/1/4 10:03
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTransferContinueRecParams extends OrderReceiveParams {

    /**
     * 文件hash值
     */
    private String fileHash;
}
