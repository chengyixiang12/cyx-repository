import { get, post, put, del } from '@/utils/http';
import { SaveUserRequest, EditUserRequest, GetUserVo, AllUserVo, EditSelfRequest } from '@/types/user';
import { PaginatedData } from '@/types/api'
import { ApiResponse, UserInfoVo } from '@/types/login';

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
 * 修改用户
 * @param params 
 */
export async function updateUserApi(params: EditUserRequest): Promise<any> {
   return put('/user', params, { flag: true })
}

/**
 * 获取用户
 * @param id 
 * @returns 
 */
export async function getUser(id: string): Promise<GetUserVo> {
   const res = await get<GetUserVo>('/user/getUser', { flag: true, params: { id } })
   return res.data;
}

/**
 * 删除用户
 * @param param 
 * @returns 
 */
export async function deleteUserById(id: string): Promise<ApiResponse<any>> {
   return await del('/user', null, { params: { id }, flag: true })
}

/**
 * 锁定用户
 * @param id 
 */
export async function lockUserApi(id: string) {
   await get('/user/lockUser', { flag: true, params: { id } })
}

/**
 * 解锁用户
 * @param id 
 */
export async function unlockUserApi(id: string) {
   await get('/user/unlockUser', { flag: true, params: { id } })
}

/**
 * 启用用户
 * @param id 
 */
export async function enableUserApi(id: string) {
   await get('/user/enableUser', { flag: true, params: { id } })
}

/**
 * 禁用用户
 * @param id 
 */
export async function forbiddenUser(id: string) {
   await get('/user/forbiddenUser', { flag: true, params: { id } })
}

/**
 * 获取用户头像
 * @param params 
 */
export async function getAvatarApi(id: string): Promise<string> {
   const res = await get('/user/getAvatar', { flag: true,  params: { id } })
   return res.data;
}

/**
* 获取登录用户信息
* @param params 登录参数
*/
export async function getUserInfoApi(): Promise<UserInfoVo> {
  const res = await get<UserInfoVo>('/user/getUserInfo', { flag: true });
  return res.data;
}

/**
 * 修改用户密码
 * @param params 
 */
export async function updatePasswordApi(data: { originalPass: string, targetPass: string }): Promise<any> {
  return put('/user/editPassword', data, { flag: true })
}

/**
 * 重置用户密码
 * @param params 
 */
export async function resetPasswordApi(id: number) {
   return get('/user/resetPassword', { flag: true, params: { id } })
}

/**
 * 获取用户
 * @param id 
 * @returns 
 */
export async function getUserApi(id: string): Promise<GetUserVo> {
   const res = await get<GetUserVo>('/user/getUser', { flag: true, params: { id } })
   return res.data;
}

/**
 * 修改个人信息
 * @param params 
 */
export async function editSelfApi(params: EditSelfRequest): Promise<any> {
   return put('/user/editSelf', params, { flag: true })
}