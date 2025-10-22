package com.soft.base.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cyq
 * @date 2025/10/22
 * @description
 */
@Getter
public enum LocationEnum {

    MINIO(1, "minio"),
    DISK(2, "磁盘")
    ;

    private final Integer code;

    private final String name;

    public static final Map<Integer, LocationEnum> map = new HashMap<>();

    LocationEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    static {
        for (LocationEnum value : LocationEnum.values()) {
            map.put(value.code, value);
        }
    }
}
