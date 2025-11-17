import { post, get, del, getBlob } from '@/utils/http';
import { FilesRequest, FilesVo, UploadAvatarVo, UploadFileVo } from '@/types/file';
import { PaginatedData } from '@/types/api';

/**
 * 根据id获取文件
 * @param id 
 * @returns 
 */
export async function getFileByIdApi(id: string): Promise<Blob> {
  const res = await get<Blob>('/file/downloadFile', { flag: true, params: { id }, responseType: 'blob'});
  return res.data;
}

/**
 * 上传文件
 * @param params 
 */
export async function uploadFileApi(params: FormData): Promise<UploadFileVo> {
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
 * 获取我的文件（复）
 * @param data 
 * @returns 
 */
export async function getMyFilesApi(data: FilesRequest): Promise<PaginatedData<FilesVo>> {
  const res = await post<PaginatedData<FilesVo>>('/file/getMyFiles', data, { flag: true });
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

/**
 * 上传用户头像
 * @param params 
 * @returns 
 */
export async function uploadAvatarApi(params: FormData): Promise<UploadAvatarVo> {
  const res = await post<UploadAvatarVo>('/user/uploadAvatar', params, { flag: true })
  return res.data;
}