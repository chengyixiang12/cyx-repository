import { PaginatedData } from '@/types/api';
import { SetPermissionsRequest } from '@/types/permissionts';
import { EditRoleRequest, GetRoleSelectVo, GetRolesRequest, SaveRoleRequest, SetMenusRequest, SysRoleVo, SysRolesVo } from '@/types/role';
import { get, post, put, del } from '@/utils/http';

/**
 * 获取角色列表
 * @param data 
 * @returns 
 */
export async function getRolesApi(data: GetRolesRequest): Promise<PaginatedData<SysRolesVo>> {
    const res = await post<PaginatedData<SysRolesVo>>('/role/getRoles', data, { flag: true });
    return res.data;
}

/**
 * 根据id获取角色
 * @param id 
 * @returns 
 */
export async function getRoleApi(id: number | null): Promise<SysRoleVo> {
    const res = await get<SysRoleVo>(`/role/${id}`, { flag: true})
    return res.data;
}

/**
 * 添加角色
 * @param params 
 */
export async function addRoleApi(data: SaveRoleRequest) {
    await post('/role', data, { flag: true })
}

/**
 * 编辑角色
 * @param data 
 */
export async function updateRoleApi(data: EditRoleRequest) {
    await put('/role', data, { flag: true })
}

/**
 * 
 * @param id 删除角色
 */
export async function deleteRoleApi(id: number) {
    await del('/role', { flag: true, params: { id } })
}

/**
 * 启用角色
 * @param id 
 */
export async function enableRoleApi(id :number) {
    await get(`/role/enableRole/${id}`, { flag: true })
}

/**
 * 禁用角色
 * @param id 
 */
export async function forbiddenRoleApi(id :number) {
    await get(`/role/forbiddenRole/${id}`, { flag: true })
}

/**
 * 设置默认角色
 * @param id 
 */
export async function setDefaultRoleApi(id :number) {
    await get(`/role/setDefaultRole/${id}`, { flag: true })
}

/**
 * 设置固定角色
 * @param id 
 */
export async function setFixRoleApi(id :number) {
    await get('/role/setFixRole', { flag: true, params: { id } })
}

/**
 * 取消固定角色
 * @param id 
 */
export async function cancelFixRoleApi(id :number) {
    await get('/role/cancelFixRole', { flag: true, params: { id } })
}

/**
 * 赋予权限
 * @param data 
 */
export async function updateRolePermissionsApi(data: SetPermissionsRequest) {
    await post('/role/setPermissions', data, { flag: true })
}

/**
 * 赋予菜单
 * @param data 
 */
export async function updateRoleMenusApi(data: SetMenusRequest) {
    await post('/role/setMenus', data, { flag: true })
}

/**
 * 获取角色的下拉框数据
 */
export async function getRoleSelectApi(): Promise<GetRoleSelectVo[]> {
    const res = await get<GetRoleSelectVo[]>('/role/getRoleSelect', { flag: true })
    return res.data
}