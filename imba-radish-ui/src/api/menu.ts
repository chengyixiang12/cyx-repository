import type { GetMenuListVo, GetMenuListRequest, GetMenuVo, MenuItem } from '@/types/menu'
import { get, post } from '@/utils/http'
import { PaginatedData } from '@/types/api'

export async function getMenuList(params: GetMenuListRequest): Promise<PaginatedData<GetMenuListVo>> {
    const getMenuListVo = await post<PaginatedData<GetMenuListVo>>('/menu/getMenuList', params, { flag: true });
    return getMenuListVo.data;
}

export async function getMenuApi(id: number): Promise<GetMenuVo> {
    const getMenuVo = await get<GetMenuVo>('menu/getMenu', { params: { id }, flag: true });
    return getMenuVo.data;
}

export async function getMenuTreeApi(): Promise<MenuItem[]> {
    const getMenuItem = await get<MenuItem[]>('/menu/getMenuTree', { flag: true })
    return getMenuItem.data;
}