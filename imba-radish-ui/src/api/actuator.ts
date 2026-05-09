import { ListActuatorPageRequest, ListActuatorVO } from '@/types/actuator';
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

export async function listActuatorPageApi(data: ListActuatorPageRequest): Promise<PaginatedData<ListActuatorVO>> {
    const res = await post<PaginatedData<ListActuatorVO>>('/actuator/listActuatorPage', data, { flag: true });
    return res.data;
}