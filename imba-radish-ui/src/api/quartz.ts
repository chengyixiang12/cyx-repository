import { PaginatedData } from '@/types/api';
import { GetQuartzTasksRequest, GetQuartzTasksVo } from '@/types/quartz';
import { get, put, post, del } from '@/utils/http'

export async function getQuartzTasksApi(data: GetQuartzTasksRequest): Promise<PaginatedData<GetQuartzTasksVo>> {
    const res = await post<PaginatedData<GetQuartzTasksVo>>('/scheduleJob/getQuartzTasks', data, { flag: true });
    return res.data;
}