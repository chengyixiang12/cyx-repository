package com.soft.base.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/26 16:48
 **/

@Getter
public enum SecretKeyEnum {

    USER_PASSWORD_KEY(0,"用户密码密钥"),
    ;

    private final Integer type;

    private final String name;

    SecretKeyEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
