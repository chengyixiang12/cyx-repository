import { PaginatedData } from '@/types/api';
import {
    HandleWorkflowRequest,
    StartWorkflowRequest,
    TransferWorkflowRequest,
    WfInstancesVo,
    WfMyInstancesRequest,
    WfRecordsVo,
    WfTasksRequest,
    WfTasksVo,
    WfTraceVo,
    WfUserOptionVo
} from '@/types/workflow';
import { get, post } from '@/utils/http';

/**
 * 获取待办任务列表
 * @param data
 * @returns
 */
export async function getWorkflowTodosApi(data: WfTasksRequest): Promise<PaginatedData<WfTasksVo>> {
    const res = await post<PaginatedData<WfTasksVo>>('/workflow/getTodos', data, { flag: true });
    return res.data;
}

/**
 * 获取已办任务列表
 * @param data
 * @returns
 */
export async function getWorkflowDonesApi(data: WfTasksRequest): Promise<PaginatedData<WfTasksVo>> {
    const res = await post<PaginatedData<WfTasksVo>>('/workflow/getDones', data, { flag: true });
    return res.data;
}

/**
 * 获取我发起的流程列表
 * @param data
 * @returns
 */
export async function getMyWorkflowInstancesApi(data: WfMyInstancesRequest): Promise<PaginatedData<WfInstancesVo>> {
    const res = await post<PaginatedData<WfInstancesVo>>('/workflow/getMyInstances', data, { flag: true });
    return res.data;
}

/**
 * 流程跟踪
 * @param id 流程实例id
 * @returns
 */
export async function getWorkflowTraceApi(id: string): Promise<WfTraceVo> {
    const res = await get<WfTraceVo>('/workflow/getTrace', { flag: true, params: { id } });
    return res.data;
}

/**
 * 获取审批人下拉选项
 * @returns
 */
export async function getApproverOptionsApi(): Promise<WfUserOptionVo[]> {
    const res = await get<WfUserOptionVo[]>('/workflow/getApproverOptions', { flag: true });
    return res.data;
}

/**
 * 发起流程
 * @param data
 */
export async function startWorkflowApi(data: StartWorkflowRequest) {
    await post('/workflow/start', data, { flag: true });
}

/**
 * 通过任务
 * @param data
 */
export async function approveWorkflowApi(data: HandleWorkflowRequest) {
    await post('/workflow/approve', data, { flag: true });
}

/**
 * 驳回任务
 * @param data
 */
export async function rejectWorkflowApi(data: HandleWorkflowRequest) {
    await post('/workflow/reject', data, { flag: true });
}

/**
 * 转办任务
 * @param data
 */
export async function transferWorkflowApi(data: TransferWorkflowRequest) {
    await post('/workflow/transfer', data, { flag: true });
}
