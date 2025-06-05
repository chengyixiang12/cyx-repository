import { PaginatedData } from '@/types/api';
import { GetDialogueHistoriesRequest, GetDialogueHistoriesVo, SaveDialogueRequest } from '@/types/dialogueHistory';
import { get, post, del, put } from '@/utils/http'

/**
 * 获取历史对话（复）
 * @param data 
 * @returns 
 */
export async function getDialogueHistoriesApi(data: GetDialogueHistoriesRequest): Promise<PaginatedData<GetDialogueHistoriesVo>> {
    const res = await post<PaginatedData<GetDialogueHistoriesVo>>('/dialogue/getDialogues', data, { flag: true });
    return res.data
}

/**
 * 新增对话
 * @param data 
 * @returns 
 */
export async function saveDialogueApi(data: SaveDialogueRequest): Promise<number | null> {
    const res = await post<number | null>('/dialogue/saveDialogue', data, { flag: true })
    return res.data
}

/**
 * 删除对话
 * @param param 
 */
export async function deleteDialogueApi(param: number) {
    del('/dialogue/deleteDialogue', { flag: true, params: { id: param } })
}