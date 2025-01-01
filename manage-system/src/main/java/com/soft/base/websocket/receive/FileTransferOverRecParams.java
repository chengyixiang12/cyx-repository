package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 文件传输结束接收参数
 * @DateTime: 2024/12/30 20:15
 **/

@EqualsAndHashCode(callSuper = false)
@Data
public class FileTransferOverRecParams extends OrderReceiveParams {

    /**
     * 源文件名
     */
    private String originalName;
}
