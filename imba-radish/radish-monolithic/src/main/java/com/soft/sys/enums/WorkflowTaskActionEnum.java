package com.soft.sys.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 工作流任务办理动作枚举
 * @DateTime: 2026/11/19 11:30
 **/
@Getter
public enum WorkflowTaskActionEnum {

    /** 通过 */
    APPROVE(1, "通过"),
    /** 驳回 */
    REJECT(2, "驳回"),
    /** 转办 */
    TRANSFER(3, "转办"),
    ;

    private final Integer code;

    private final String name;

    WorkflowTaskActionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
