import { PaginatedData } from '@/types/api';
import { GetLogVo, LogsRequest, LogsVo } from '@/types/log';
import { get, del, post } from '@/utils/http'

/**
 * 获取日志（复）
 * @param data 
 */
export async function getLogsApi(data: LogsRequest): Promise<PaginatedData<LogsVo>> {
    const res = await post('/log/getLogs', data, { flag: true });
    return res.data;
}

/**
 * 删除日志
 * @param id 
 */
export async function deleteLogApi(id: number) {
    await del('/log', { flag: true, params: { id } });
}

/**
 * 获取日志
 * @param id 
 */
export async function getLogDetailApi(id: number): Promise<GetLogVo> {
    const res = await get('/log/getLog', { flag: true, params: { id } });
    return res.data;
}