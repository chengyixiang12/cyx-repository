import { PaginatedData } from '@/types/api';
import { GetAssignPerVo, GetAllPermissionVo, PermissionsRequest, PermissionsVo, SavePermissionRequest, EditPermissionRequest, GetPermissionVo } from '@/types/permission';
import { get, post, put, del } from '@/utils/http';

/**
 * 获取所有权限
 * @param id 
 * @returns 
 */
export async function getAllPermissionsApi(): Promise<GetAllPermissionVo[]> {
    const res = await get<GetAllPermissionVo[]>('/permission/getAllPermission', { flag: true });
    return res.data;
}

/**
 * 获取被赋予的权限
 * @param id 
 * @returns 
 */
export async function getAssignPerApi(id: string): Promise<GetAssignPerVo[]> {
    const res = await get<GetAssignPerVo[]>('/permission/getAssignPer', { flag: true, params: { roleId: id } });
    return res.data;
}

/**
 * 获取权限列表
 * @param data 
 * @returns 
 */
export async function getPermissionsApi(data: PermissionsRequest): Promise<PaginatedData<PermissionsVo>> {
    const res = await post<PaginatedData<PermissionsVo>>('/permission/getPermissions', data, { flag: true });
    return res.data;
}

/**
 * 新增权限
 * @param data 
 */
export async function savePermissionApi(data: SavePermissionRequest) {
    await post('/permission', data, { flag: true });
}

/**
 * 编辑权限
 * @param data 
 */
export async function editPermissionApi(data: EditPermissionRequest) {
    await put('/permission', data, { flag: true });
}

/**
 * 删除权限
 * @param id 
 */
export async function deletePermissionApi(id: string) {
    await del('/permission', { params: { id }, flag: true });
}

/**
 * 启用权限
 * @param id 
 */
export async function enablePermissionApi(id: string) {
    await get('/permission/enablePermission', { params: { id }, flag: true });
}

/**
 * 禁用权限
 * @param id 
 */
export async function forbiddenPermissionApi(id: string) {
    await get('/permission/forbiddenPermission', { params: { id }, flag: true });
}

/**
 * 获取权限详情
 * @param id 
 * @returns 
 */
export async function getPermissionApi(id: string): Promise<GetPermissionVo> {
    const res = await get<GetPermissionVo>('/permission/getPermission', { flag: true, params: { id } });
    return res.data;
}