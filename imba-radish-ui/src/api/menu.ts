import { PaginatedData } from '@/types/api';
import type { EditMenuRequest, GetAssignedMenuVo, GetMenuListRequest, GetMenuListVo, GetMenuTreeVo, GetMenuVo, GetSelectMenuVo, SaveMenuRequest } from '@/types/menu'
import { get, post, del, put } from '@/utils/http'

/**
 * 获取菜单列表
 * @param params 
 * @returns 
 */
export async function getMenuList(params: GetMenuListRequest): Promise<PaginatedData<GetMenuListVo>> {
    const res = await post<PaginatedData<GetMenuListVo>>('/menu/getMenuList', params, { flag: true });
    return res.data;
}

/**
 * 获取菜单详情
 * @param id 
 * @returns 
 */
export async function getMenuApi(id: number): Promise<GetMenuVo> {
    const res = await get<GetMenuVo>('menu/getMenu', { params: { id }, flag: true });
    return res.data;
}

/**
 * 获取菜单树结构
 * @returns 
 */
export async function getSelectMenu(type?: string): Promise<GetSelectMenuVo[]> {
    const res = await get<GetSelectMenuVo[]>('/menu/getSelectMenu', { flag: true, params: { type } })
    return res.data;
}

/**
 * 删除菜单
 * @param param 
 */
export async function deleteMenuApi(param: number) {
    await del(`/menu/${param}`, { flag: true });
}

/**
 * 编辑菜单
 * @param data 
 */
export async function updateMenuStatusApi(data: EditMenuRequest) {
    await put('/menu', data, { flag: true })
}

/**
 * 新增菜单
 * @param data 
 */
export async function addMenuStatusApi(data: SaveMenuRequest) {
    await post('/menu', data, { flag: true })
}

/**
 * 启用菜单
 * @param id 
 */
export async function enableMenuApi(id:number) {
    await get(`/menu/enableMenu/${id}`, { flag: true })
}

/**
 * 禁用菜单
 * @param id 
 */
export async function disableMenuApi(id:number) {
    await get(`/menu/disableMenu/${id}`, { flag: true })
}

/**
 * 获取所有的菜单
 */
export async function getAllMenuTreeApi(): Promise<GetMenuTreeVo[]> {
    const res = await get<GetMenuTreeVo[]>('/menu/getMenuTree', { flag: true })
    return res.data;
}

/**
 * 获取已赋予的菜单
 * @param roleId 
 */
export async function getAssignedMenuApi(roleId: number): Promise<GetAssignedMenuVo[]> {
    const res = await get<GetAssignedMenuVo[]>('/menu/getAssignedMenu', { flag: true, params: { roleId } });
    return res.data;
}