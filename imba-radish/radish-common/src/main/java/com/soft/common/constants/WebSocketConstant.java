package com.soft.common.constants;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/21 23:03
 **/
public class WebSocketConstant {

    /**
     * 用于存储在websocket中的用户信息
     */
    public final static String WEBSOCKET_USER = "ws_user";

    /**
     * websocket鉴权
     */
    public final static String AUTHORIZATION = "Authorization";

    /**
     * 设备类型（web / app / mini-program），握手参数与 session attributes 共用
     */
    public final static String DEVICE_TYPE = "deviceType";
}
