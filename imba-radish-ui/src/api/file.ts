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
  const res = await post<UploadFileVo>('/file/upload', params, { flag: true, headers: { 'Content-Type': 'multipart/form-data'} })
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
  console.log('获取我的文件')
  const res = await post<PaginatedData<FilesVo>>('/file/getMyFiles', data, { flag: true });
  return res.data;
}

/**
 * 删除文件
 * @param id 
 */
export async function deleteFileApi(id: string) {
  console.log('执行删除')
  del('/file', null, { params: { id }, flag: true })
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
 * @param data 
 * @returns 
 */
export async function uploadAvatarApi(data: FormData): Promise<UploadAvatarVo> {
  const res = await post<UploadAvatarVo>('/file/uploadAvatar', data, { flag: true, headers: { 'Content-Type': 'multipart/form-data'} });
  return res.data;
}

/**
 * 上传分片
 * @param fileMd5 
 * @param chunkIndex 
 * @param chunk 
 * @returns 
 */
export async function uploadChunkApi(formData: FormData) {
  await post<any>('/file/uploadChunk', formData, { flag: true, headers: { 'Content-Type': 'multipart/form-data'} });
}

/**
 * 合并分片
 * @param fileMd5 
 * @param fileName 
 * @param total 
 */
export async function mergeChunkApi(fileMd5: string, fileName: string, total: number): Promise<UploadFileVo> {
  const res = await get<UploadFileVo>('/file/mergeChunk', { flag: true, params: { fileMd5, fileName, total}});
  return res.data;
}

/**
 * 获取文件url
 * @param id 
 * @param isInline 
 * @returns 
 */
export async function getFileUrlApi(id: string, isInline: string): Promise<string> {
  const res = await get<string>('/file/getFileUrl', { flag: true, params: { id, isInline}});
  return res.data;
}