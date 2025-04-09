import type { GetMenuListVo, GetMenuListRequest, GetMenuVo, MenuItem } from '@/types/menu'
import { get, post, del } from '@/utils/http'
import { PaginatedData } from '@/types/api'

export async function getMenuList(params: GetMenuListRequest): Promise<PaginatedData<GetMenuListVo>> {
    const res = await post<PaginatedData<GetMenuListVo>>('/menu/getMenuList', params, { flag: true });
    return res.data;
}

export async function getMenuApi(id: number): Promise<GetMenuVo> {
    const res = await get<GetMenuVo>('menu/getMenu', { params: { id }, flag: true });
    return res.data;
}

export async function getMenuTreeApi(): Promise<MenuItem[]> {
    const res = await get<MenuItem[]>('/menu/getMenuTree', { flag: true })
    return res.data;
}

export async function deleteMenuApi(param: number) {
    await del(`/menu/${param}`, { flag: true });
}