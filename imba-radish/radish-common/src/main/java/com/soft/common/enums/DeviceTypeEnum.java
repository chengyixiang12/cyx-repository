package com.soft.common.enums;

import lombok.Getter;

/**
 * 设备类型。
 *
 * <p>同一账号允许不同类型设备同时在线（web / app / mini-program），
 * 同一类型设备互斥：新登录会踢掉该类型下旧的 token 和 WebSocket 会话。</p>
 *
 * @author cyx
 */
@Getter
public enum DeviceTypeEnum {

    /** 网页 */
    WEB("web"),

    /** App */
    APP("app"),

    /** 小程序 */
    MINI_PROGRAM("mini-program"),
    ;

    /**
     * 客户端传递的设备类型标识
     */
    private final String type;

    DeviceTypeEnum(String type) {
        this.type = type;
    }

    /**
     * 规范化设备类型：空值或未知值统一回退为 web，兼容未升级的旧客户端。
     */
    public static String normalize(String type) {
        if (type == null || type.isBlank()) {
            return WEB.type;
        }
        String value = type.trim();
        for (DeviceTypeEnum deviceType : values()) {
            if (deviceType.type.equalsIgnoreCase(value)) {
                return deviceType.type;
            }
        }
        return WEB.type;
    }
}
