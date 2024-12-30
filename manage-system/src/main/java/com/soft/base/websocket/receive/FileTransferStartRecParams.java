package com.soft.base.websocket.receive;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 文件传输开始接收参数
 * @DateTime: 2024/12/30 15:37
 **/

@Deprecated
@Data
public class FileTransferStartRecParams {

    /**
     * 文件名
     */
    @NotNull
    private String fileName;
}
