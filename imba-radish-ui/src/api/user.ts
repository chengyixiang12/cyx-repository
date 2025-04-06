import { get, post, put, del } from '@/utils/http';
import { SaveUserRequest, EditUserRequest, GetUserVo, AllUserVo } from '@/types/user';
import { PaginatedData, PaginatedResponse } from '@/types/api'
import { ApiResponse } from '@/types/login';

export async function getUserList(data: any): Promise<PaginatedData<AllUserVo>> {
   const users = await post<PaginatedData<AllUserVo>>('/user/getAllUsers', data, { flag: true });
   return users.data;
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
   const getUserVo = await get<GetUserVo>(`/user/getUser/${param}`, { flag: true })
   return getUserVo.data;
}

/**
 * 删除用户
 * @param param 
 * @returns 
 */
export async function deleteUserById(param: number): Promise<ApiResponse<any>> {
   return await del('/user', { params: { id: param }, flag: true })
}