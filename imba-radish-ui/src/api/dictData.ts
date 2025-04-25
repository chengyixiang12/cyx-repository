import { PaginatedData } from '@/types/api';
import { DictDatasRequest, DictDatasVo, DictDataVo, EditDictDataRequest, SaveDictDataRequest } from '@/types/dictData';
import { get, post, put, del } from '@/utils/http'

/**
 * 获取字典数据（复）
 * @param data 
 */
export async function getDictDatasApi(data: DictDatasRequest): Promise<PaginatedData<DictDatasVo>> {
    const res = await post<PaginatedData<DictDatasVo>>('/dictData/getDictDatas', data, { flag: true });
    return res.data;
}

/**
 * 删除字典数据
 * @param id 
 */
export async function deleteDictDataApi(id: number) {
    await del(`/dictData/${id}`, { flag: true })
}

/**
 * 启用字典数据
 * @param id 
 */
export async function enableDictDataApi(id: number) {
    await get('/dictData/enableDictData', { flag: true, params: { id } });
}

/**
 * 禁用字典数据
 * @param id 
 */
export async function forbiddenDictDataApi(id: number) {
    await get('/dictData/forbiddenDictData', { flag: true, params: { id } });
}

/**
 * 添加字典数据
 * @param data 
 */
export async function saveDictDataApi(data: SaveDictDataRequest) {
    await post('/dictData', data, { flag: true });
}

/**
 * 编辑字典数据
 * @param data 
 */
export async function editDictDataApi(data: EditDictDataRequest) {
    await put('/dictData', data, { flag: true });
}

/**
 * 获取字典数据（单）
 * @param id 
 */
export async function getDictDataApi(id: number): Promise<DictDataVo> {
    const res = await get(`/dictData/${id}`, { flag: true });
    return res.data;
}

/**
 * 设置默认
 * @param id 
 * @param dictType 
 */
export async function setDefaultRoleApi(id: number, dictType: string) {
    await get('/dictData/setDefaultData', { flag: true, params: { id, dictType } })
}