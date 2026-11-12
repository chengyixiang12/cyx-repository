import type { AxiosProgressEvent } from 'axios'
import { post, get, del, getBlob } from '@/utils/http';
import { ChunkProgressVo, FilesRequest, FilesVo, UploadFileVo } from '@/types/file';
import { PaginatedData } from '@/types/api';

/** 单个分片 / 小文件 / 合并的超时都要比全局 60s 长，慢网络下 5MB 分片很容易超时 */
const CHUNK_TIMEOUT = 5 * 60 * 1000;
const FILE_TIMEOUT = 10 * 60 * 1000;
// 合并是同步写 MinIO，2GB 文件可能要跑几分钟
const MERGE_TIMEOUT = 30 * 60 * 1000;

export interface UploadRequestOptions {
  signal?: AbortSignal;
  /** 已上传字节数（由 axios 的 onUploadProgress 提供） */
  onUploadProgress?: (loaded: number) => void;
  timeout?: number;
}

export interface MergeChunkParams {
  fileMd5: string;
  fileName: string;
  total: number;
  fileSize: number;
  chunkSize: number;
  signal?: AbortSignal;
}

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
 * 上传文件（小文件直传）
 */
export async function uploadFileApi(params: FormData, options: UploadRequestOptions = {}): Promise<UploadFileVo> {
  const res = await post<UploadFileVo>('/file/upload', params, {
    flag: true,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: options.timeout ?? FILE_TIMEOUT,
    signal: options.signal,
    onUploadProgress: (event: AxiosProgressEvent) => options.onUploadProgress?.(event.loaded ?? 0)
  })
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
export async function deleteFileApi(id: string): Promise<void> {
  await del('/file', null, { params: { id }, flag: true })
}

/**
 * 下载文件
 * @param id 文件主键
 * @param silent 静默模式：失败时不弹全局错误提示（用于头像等背景加载场景）
 * @returns
 */
export async function downloadFileApi(id: string, silent = false): Promise<Blob> {
  const blob = await getBlob<Blob>('/file/downloadFile', { flag: true, params: { id }, silent });
  return blob;
}

/**
 * 上传单个分片
 */
export async function uploadChunkApi(formData: FormData, options: UploadRequestOptions = {}): Promise<void> {
  await post<void>('/file/uploadChunk', formData, {
    flag: true,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: options.timeout ?? CHUNK_TIMEOUT,
    signal: options.signal,
    onUploadProgress: (event: AxiosProgressEvent) => options.onUploadProgress?.(event.loaded ?? 0)
  })
}

/**
 * 合并分片（后端已改为 POST，副作用接口不再用 GET）
 */
export async function mergeChunkApi(params: MergeChunkParams): Promise<UploadFileVo> {
  const { signal, ...query } = params
  const res = await post<UploadFileVo>('/file/mergeChunk', undefined, {
    flag: true,
    params: query,
    timeout: MERGE_TIMEOUT,
    signal
  })
  return res.data;
}

/**
 * 获取文件url
 * @param id 
 * @param isInline 是否缩略图展示
 * @returns 
 */
export async function getFileUrlApi(id: string, isInline: string): Promise<string> {
  const res = await get<string>('/file/getFileUrl', { flag: true, params: { id, isInline}});
  return res.data;
}

/**
 * 根据md5获取文件（秒传）；命中时返回文件 id，未命中返回 null
 */
export async function getFileByMd5Api(fileMd5: string, fileName: string, signal?: AbortSignal): Promise<string | null> {
  const res = await get<string | null>('/file/getFileByMd5', { flag: true, params: { fileMd5, fileName }, signal });
  return res.data;
}

/**
 * 校据md5判断文件是否存在
 * @param fileMd5 
 * @param fileName 
 * @returns 
 */
export async function existFileApi(fileMd5: string, fileName: string): Promise<boolean> {
  const res = await get<boolean>('/file/existFile', { flag: true, params: { fileMd5, fileName }});
  return res.data;
}

/**
 * 查询可续传的分片；带 fileSize/chunkSize，后端会校验分片长度，损坏的旧分片不会被算作已完成
 */
export async function getUploadProgressApi(
  fileMd5: string,
  fileSize: number,
  chunkSize: number,
  signal?: AbortSignal
): Promise<ChunkProgressVo> {
  const res = await get<ChunkProgressVo>('/file/getUploadProgress', {
    flag: true,
    params: { fileMd5, fileSize, chunkSize },
    signal
  });
  return res.data;
}

/**
 * 取消上传时清理服务端已落盘的分片（失败也无所谓，服务端 TTL 会兜底回收）
 */
export async function cancelChunkApi(fileMd5: string): Promise<void> {
  await del('/file/cancelChunk', null, { params: { fileMd5 }, flag: true, silent: true })
}
