import { post, get, del, getBlob } from '@/utils/http';
import { FilesRequest, FilesVo, UploadFileVo } from '@/types/file';
import { PaginatedData } from '@/types/api';

/**
 * 根据id获取文件
 * @param id 
 * @returns 
 */
export async function getFileById(id: string): Promise<Blob> {
  const res = await get<Blob>('/file/downloadFile', { flag: true, params: { id }, responseType: 'blob'});
  return res.data;
}

/**
 * 上传文件
 * @param params 
 */
export async function uploadFile(params: FormData): Promise<UploadFileVo> {
  const res = await post<UploadFileVo>('/file', params, { flag: true })
  return res.data;
}

/**
 * 获取文件（复）
 * @param data 
 * @returns 
 */
export async function getFilesApi(data: FilesRequest): Promise<PaginatedData<FilesVo>> {
  const res = await post<PaginatedData<FilesVo>>('/file/getFiles', data, { flag: true });
  return res.data;
}

/**
 * 删除文件
 * @param id 
 */
export async function deleteFileApi(id: string) {
  del('/file', { params: { id }, flag: true })
}

/**
 * 下载文件
 * @param id 
 * @returns 
 */
export async function downloadFileApi(id: string): Promise<Blob> {
  const blob = await getBlob<Blob>('/file/downloadFile', { flag: true, params: { id }});
  return blob;
}