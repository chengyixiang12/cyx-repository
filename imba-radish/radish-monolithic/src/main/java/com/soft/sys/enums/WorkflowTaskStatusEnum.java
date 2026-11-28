package com.soft.sys.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 工作流任务状态枚举
 * @DateTime: 2026/11/19 11:30
 **/
@Getter
public enum WorkflowTaskStatusEnum {

    /** 待办 */
    TODO(1, "待办"),
    /** 已办 */
    DONE(2, "已办"),
    /** 已取消 */
    CANCELLED(3, "已取消"),
    ;

    private final Integer code;

    private final String name;

    WorkflowTaskStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
