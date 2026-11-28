import { PageRequest } from './api';

/**
 * 待办/已办任务列表请求
 */
export interface WfTasksRequest extends PageRequest {
    keyword: string;
}

/**
 * 我发起的流程列表请求
 */
export interface WfMyInstancesRequest extends PageRequest {
    keyword: string;
    status: number | null;
}

/**
 * 待办/已办任务行
 */
export interface WfTasksVo {
    id: string;
    instanceId: string;
    processKey: string;
    processName: string;
    title: string;
    businessKey: string;
    currentNode: string;
    taskName: string;
    arriveTime: string;
    handleTime: string;
    action: number | null;
    comment: string;
    startUserNickname: string;
    assigneeNickname: string;
}

/**
 * 我发起的流程行
 */
export interface WfInstancesVo {
    id: string;
    processKey: string;
    processName: string;
    title: string;
    businessKey: string;
    status: number;
    currentNode: string;
    currentAssigneeNickname: string;
    createTime: string;
    endTime: string;
}

/**
 * 审批留痕行
 */
export interface WfRecordsVo {
    nodeName: string;
    operatorNickname: string;
    action: string;
    comment: string;
    operateTime: string;
}

/**
 * 流程跟踪详情
 */
export interface WfTraceVo {
    id: string;
    processKey: string;
    processName: string;
    title: string;
    businessKey: string;
    status: number;
    currentNode: string;
    engine: string;
    startUserNickname: string;
    createTime: string;
    endTime: string;
    tasks: WfTasksVo[];
    records: WfRecordsVo[];
}

/**
 * 发起流程请求
 */
export interface StartWorkflowRequest {
    processKey: string;
    processName: string;
    businessKey?: string;
    title?: string;
    assigneeIds: string[];
    comment?: string;
}

/**
 * 办理任务请求（通过/驳回）
 */
export interface HandleWorkflowRequest {
    taskId: string;
    comment?: string;
}

/**
 * 转办任务请求
 */
export interface TransferWorkflowRequest {
    taskId: string;
    targetUserId: string;
    comment?: string;
}

/**
 * 审批人下拉选项
 */
export interface WfUserOptionVo {
    id: string;
    nickname: string;
}
