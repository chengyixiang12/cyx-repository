package com.soft.base.websocket.send;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

/**
 * @author cyq
 * @date 2025/10/31
 * @description
 */
@Data
public abstract class AbstractSendParams {

    /**
     * 状态
     */
    private Boolean status = true;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 指令
     */
    private String order;

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
