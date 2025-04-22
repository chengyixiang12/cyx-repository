import { GetAssignPerVo, GetAllPermissionVo } from '@/types/permissionts';
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
export async function getAssignPerApi(id: number): Promise<GetAssignPerVo[]> {
    const res = await get<GetAssignPerVo[]>('/permission/getAssignPer', { flag: true, params: { roleId: id } });
    return res.data;
}