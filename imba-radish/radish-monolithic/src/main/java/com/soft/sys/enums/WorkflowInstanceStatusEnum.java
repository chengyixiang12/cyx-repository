package com.soft.sys.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 工作流实例状态枚举
 * @DateTime: 2026/11/19 11:30
 **/
@Getter
public enum WorkflowInstanceStatusEnum {

    /** 运行中 */
    RUNNING(1, "运行中"),
    /** 已完成 */
    FINISHED(2, "已完成"),
    /** 已终止 */
    TERMINATED(3, "已终止"),
    ;

    private final Integer code;

    private final String name;

    WorkflowInstanceStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
