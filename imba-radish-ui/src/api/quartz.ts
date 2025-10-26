import { PaginatedData } from '@/types/api';
import { EditJobRequest, GetJobVo, GetQuartzTasksRequest, GetQuartzTasksVo, SaveJobRequest } from '@/types/quartz';
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

/**
 * 解析cron表达式
 * @param cron 
 * @returns 
 */
export async function parseCronApi(cron: string): Promise<string[]> {
  const res = await get<string[]>('/scheduleJob/parseCron', { flag: true, params: { cronExpress: cron } });
  return res.data;
}

/**
 * 新增任务
 * @param data 
 */
export async function createJobApi(data: SaveJobRequest): Promise<void> {
  await post('/scheduleJob/createJob', data, { flag: true });
}

/**
 * 获取任务详情
 * @param param 任务id
 * @returns 
 */
export async function getJobApi(param: number): Promise<GetJobVo> {
  const res = await get<GetJobVo>('/scheduleJob/getJob', { flag: true, params: { id: param } });
  return res.data;
}

/**
 * 修改任务
 * @param data 
 */
export async function editJobApi(data: EditJobRequest): Promise<void> {
  await put('/scheduleJob/editJob', data, { flag: true });
}

/**
 * 删除任务
 * @param param 任务id
 */
export async function deleteJobApi(id: number): Promise<void> {
  await del('/scheduleJob/deleteJob', { flag: true, params: { id } });
}

/**
 * 立即执行
 * @param id 
 */
export async function execImmediately(id: number): Promise<void> {
  await get('/scheduleJob/execImmediately', { flag: true, params: { id } });
}