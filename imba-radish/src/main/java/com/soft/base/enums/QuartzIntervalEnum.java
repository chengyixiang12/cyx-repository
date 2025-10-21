package com.soft.base.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cyq
 * @date 2025/9/29
 * @description
 */

public enum QuartzIntervalEnum {

    MILLISECONDS(0), SECONDS(1), MINUTES(2), HOURS(3);

    private final Integer value;

    public static final Map<Integer, QuartzIntervalEnum> map = new HashMap<>();

    QuartzIntervalEnum(Integer value) {
        this.value = value;
    }

    static {
        for (QuartzIntervalEnum value : QuartzIntervalEnum.values()) {
            map.put(value.value, value);
        }
    }
}
