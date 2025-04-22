import { get, post, put, del } from '@/utils/http';
import { SaveUserRequest, EditUserRequest, GetUserVo, AllUserVo } from '@/types/user';
import { PaginatedData } from '@/types/api'
import { ApiResponse } from '@/types/login';

export async function getUserList(data: any): Promise<PaginatedData<AllUserVo>> {
   const res = await post<PaginatedData<AllUserVo>>('/user/getUsers', data, { flag: true });
   return res.data;
}

/**
 * 新增用户
 * @param params 
 * @returns 
 */
export async function addUser(params: SaveUserRequest): Promise<any> {
   return post('/user', params, { flag: true });
}

/**
 * 
 * @param params 修改用户
 */
export async function updateUser(params: EditUserRequest): Promise<any> {
   return put('/user', params, { flag: true })
}

/**
 * 获取用户
 * @param param 
 * @returns 
 */
export async function getUser(param: number): Promise<GetUserVo> {
   const res = await get<GetUserVo>(`/user/getUser/${param}`, { flag: true })
   return res.data;
}

/**
 * 删除用户
 * @param param 
 * @returns 
 */
export async function deleteUserById(param: number): Promise<ApiResponse<any>> {
   return await del('/user', { params: { id: param }, flag: true })
}

/**
 * 锁定用户
 * @param id 
 */
export async function lockUserApi(id: number) {
   await get(`/user/lockUser/${id}`, { flag: true })
}

/**
 * 解锁用户
 * @param id 
 */
export async function unlockUserApi(id: number) {
   await get(`/user/unlockUser/${id}`, { flag: true })
}

/**
 * 启用用户
 * @param id 
 */
export async function enableUserApi(id: number) {
   await get(`/user/enableUser/${id}`, { flag: true })
}

/**
 * 禁用用户
 * @param id 
 */
export async function forbiddenUser(id: number) {
   await get(`/user/forbiddenUser/${id}`, { flag: true })
}