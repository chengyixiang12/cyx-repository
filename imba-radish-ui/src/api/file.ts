import { post, get } from '@/utils/http';
import { UploadFileVo } from '@/types/file';

/**
 * 
 * @param param 根据id获取文件
 * @returns 
 */
export async function getFileById(param?: number): Promise<Blob> {
  const res = await get<Blob>('/file/downloadFile', { flag: true, params: { id: param }, responseType: 'blob'});
  return res.data;
}

/**
 * 上传文件
 * @param params 
 */
export async function uploadFile(params?: FormData): Promise<UploadFileVo> {
  const res = await post<UploadFileVo>('/file', params, { flag: true })
  return res.data;
}