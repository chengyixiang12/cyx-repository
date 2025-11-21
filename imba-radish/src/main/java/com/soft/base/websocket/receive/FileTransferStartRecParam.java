package com.soft.base.websocket.receive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 文件传输开始接收参数
 * @DateTime: 2024/12/30 15:37
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTransferStartRecParam extends AbstractRecParam {

    /**
     * 文件hash值
     */
    private String fileHash;
}
