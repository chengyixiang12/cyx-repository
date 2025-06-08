import { PaginatedData } from '@/types/api';
import { DictTypesVo, DictTypeVo, EditDictTypeRequest, GetDictTypesRequest, SaveDictTypeRequest } from '@/types/dictType';
import { get, post, put, del } from '@/utils/http'

/**
 * 获取字典类型（复）
 * @param data 
 * @returns 
 */
export async function getDictTypesApi(data: GetDictTypesRequest): Promise<PaginatedData<DictTypesVo>> {
    const res = await post<PaginatedData<DictTypesVo>>('/dictType/getDictTypes', data, { flag: true });
    return res.data;
}

/**
 * 获取字典类型（单）
 * @param id 
 */
export async function getDictTypeApi(id: number): Promise<DictTypeVo> {
    const res = await get('/dictType', { flag: true, params: { id } });
    return res.data;
}

/**
 * 删除字典类型
 * @param id 
 */
export async function deleteDictTypeApi(id: number) {
    await del(`/dictType/${id}`, { flag: true })
}

/**
 * 启用字典类型
 * @param id 
 */
export async function enableDictTypeApi(id: number) {
    await get('/dictType/enableDictType', { flag: true, params: { id } })
}

/**
 * 禁用字典类型
 * @param id 
 */
export async function forbiddenDictTypeApi(id: number) {
    await get('/dictType/forbiddenDictType', { flag: true, params: { id } })
}

/**
 * 添加字典类型
 * @param data 
 */
export async function saveDictTypeApi(data: SaveDictTypeRequest) {
    await post('/dictType', data, { flag: true });
}

/**
 * 编辑字典类型
 * @param data 
 */
export async function editDictTypeApi(data: EditDictTypeRequest) {
    await put('/dictType', data, { flag: true })
}