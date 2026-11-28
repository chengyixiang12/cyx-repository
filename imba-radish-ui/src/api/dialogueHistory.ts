import { PaginatedData } from '@/types/api';
import { DialogueDetailsVo, GetDialogueDetailsRequest, GetDialogueHistoriesRequest, GetDialogueHistoriesVo, GetTitleVo, RenameRequest, SaveDialogueRequest } from '@/types/dialogueHistory';
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
 * @param id 
 */
export async function deleteDialogueApi(id: string) {
    del('/dialogue/deleteDialogue', null, { flag: true, params: { id } });
}

/**
 * 重命名
 * @param data 
 */
export async function renameApi(data: RenameRequest) {
    put('/dialogue/rename', data, { flag: true })
}

export async function getTitleApi(id: string | null): Promise<GetTitleVo> {
    const res = await get<GetTitleVo>('/dialogue/getTitle', { flag: true, params: { id } });
    return res.data;
}

/**
 * 分页获取对话明细（倒序分页，第 1 页为最新消息，用于向上懒加载）
 */
export async function getDialogueDetailsApi(data: GetDialogueDetailsRequest): Promise<PaginatedData<DialogueDetailsVo>> {
    const res = await post<PaginatedData<DialogueDetailsVo>>('/dialogueDetails/getDetails', data, { flag: true });
    return res.data;
}