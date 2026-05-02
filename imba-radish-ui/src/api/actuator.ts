import { Health, ListActuatorVO, Metrics } from '@/types/actuator';
import { get, getActuator } from '@/utils/http';

/**
 * 获取所有actuator信息
 * @param startTime 开始时间
 * @param endTime 结束时间
 */
export async function listActuatorApi(startTime: string, endTime: string): Promise<ListActuatorVO[]> {
    const res = await get<ListActuatorVO[]>('/actuator/listActuator', { params: { startTime, endTime }, flag: true });
    return res.data;
}