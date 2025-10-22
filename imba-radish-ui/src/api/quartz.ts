import { PaginatedData } from '@/types/api';
import { GetQuartzTasksRequest, GetQuartzTasksVo } from '@/types/quartz';
import { get, put, post, del } from '@/utils/http'

/**
 * 获取定时任务列表
 * @param data 
 * @returns 
 */
export async function getQuartzTasksApi(data: GetQuartzTasksRequest): Promise<PaginatedData<GetQuartzTasksVo>> {
    const res = await post<PaginatedData<GetQuartzTasksVo>>('/scheduleJob/getQuartzTasks', data, { flag: true });
    return res.data;
}

/**
 * 启动任务
 * @param id 任务id
 */
export async function startJobApi(id: number): Promise<void> {
  await get('/scheduleJob/startJob', { flag: true, params: { id } });
}

/**
 * 暂停任务
 * @param id 任务id
 */
export async function stopJobApi(id: number): Promise<void> {
  await get('/scheduleJob/stopJob', { flag: true, params: { id } });
}