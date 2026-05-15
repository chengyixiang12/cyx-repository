import { GetLatestActuatorMetricVO, ListActuatorPageRequest, ListActuatorVO, ListUsageTrendVO } from '@/types/actuator';
import { PaginatedData } from '@/types/api';
import { get, post } from '@/utils/http';

/**
 * 获取所有actuator信息
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
export async function listActuatorApi(startTime: string, endTime: string): Promise<ListActuatorVO[]> {
    const res = await get<ListActuatorVO[]>('/actuator/listActuator', { params: { startTime, endTime }, flag: true });
    return res.data;
}

/**
 * 获取actuator分页信息
 * @param data 分页参数
 */
export async function listActuatorPageApi(data: ListActuatorPageRequest): Promise<PaginatedData<ListActuatorVO>> {
    const res = await post<PaginatedData<ListActuatorVO>>('/actuator/listActuatorPage', data, { flag: true });
    return res.data;
}

/**
 * 获取最新actuator指标
 */
export async function getLatestActuatorMetricApi(): Promise<GetLatestActuatorMetricVO> {
    const res = await get<GetLatestActuatorMetricVO>(`/actuator/getLatestActuatorMetric`, { flag: true });
    return res.data;
}

/**
 * 获取cpu趋势
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
export async function listCpuTrendApi(startTime: string, endTime: string): Promise<ListUsageTrendVO[]> {
    const res = await get<ListUsageTrendVO[]>(`/actuator/listCpuTrend`, { params: { startTime, endTime }, flag: true });
    return res.data;
}

/**
 * 获取内存趋势
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
export async function listMemeryTrendApi(startTime: string, endTime: string): Promise<ListUsageTrendVO[]> {
    const res = await get<ListUsageTrendVO[]>(`/actuator/listMemeryTrend`, { params: { startTime, endTime }, flag: true });
    return res.data;
}
