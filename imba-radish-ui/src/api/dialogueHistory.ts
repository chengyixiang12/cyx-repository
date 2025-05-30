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

export async function saveDialogueApi(data: SaveDialogueRequest): Promise<number | null> {
    const res = await post<number | null>('/dialogue/saveDialogue', data, { flag: true })
    return res.data
}