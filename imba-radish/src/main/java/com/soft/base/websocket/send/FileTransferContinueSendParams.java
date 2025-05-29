package com.soft.base.websocket.send;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 文件传输断点续传发送消息参数
 * @DateTime: 2025/1/2 11:45
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTransferContinueSendParams extends SendParams {

    /**
     * 分片索引
     */
    private Integer index;
}
