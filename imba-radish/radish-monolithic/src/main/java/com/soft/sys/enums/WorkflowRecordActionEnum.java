package com.soft.sys.enums;

import lombok.Getter;

/**
 * @Author: cyx
 * @Description: 工作流审批留痕动作枚举
 * @DateTime: 2026/11/19 11:30
 **/
@Getter
public enum WorkflowRecordActionEnum {

    /** 发起流程 */
    START("START", "发起流程"),
    /** 通过 */
    APPROVE("APPROVE", "通过"),
    /** 驳回 */
    REJECT("REJECT", "驳回"),
    /** 转办 */
    TRANSFER("TRANSFER", "转办"),
    /** 流程完成 */
    FINISH("FINISH", "流程完成"),
    /** 流程终止 */
    TERMINATE("TERMINATE", "流程终止"),
    ;

    private final String code;

    private final String name;

    WorkflowRecordActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
