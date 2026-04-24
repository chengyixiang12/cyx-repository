import { get, post, put } from '@/utils/http'
import { GetQuartzRecordListRequest, GetQuartzRecordListVo } from '@/types/quartzRecord'
import { PaginatedData } from '@/types/api';

/**
 * 获取定时任务记录列表
 */
export async function getQuartzRecordListApi(data: GetQuartzRecordListRequest): Promise<PaginatedData<GetQuartzRecordListVo>> {
  const res = await post<PaginatedData<GetQuartzRecordListVo>>('/scheduleRecord/getQuartzRecordList', data, { flag: true });
  return res.data;
}

/**
 * 获取定时任务记录日志详情
 */
export async function getLogDetailApi(id: string): Promise<string> {
  const res = await get<string>('/scheduleRecord/getLogDetail', { flag: true, params: { id } })
  return res.data;
}

