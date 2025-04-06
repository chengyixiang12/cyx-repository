import { LoginRequest, LoginVo, UserInfoVo } from '../types/login';
import { post, get, getBlob } from '@/utils/http';
import { v4 as uuidv4 } from 'uuid';

export async function getGraphicCaptcha(uuid?: string): Promise<{ blob: Blob; uuid: string }> {
  // 生成或使用传入的唯一标识
  const finalUKey = uuid || uuidv4();
  // 调用method.ts的get方法（明确指定responseType为'blob'）
  const blob = await getBlob(
    `/auth/getGraphicCaptcha/${finalUKey}`,
    {
      flag: false
    }
  );
  return { blob, uuid: finalUKey };
}

/**
 * 用户登录
 * @param params 登录参数
 */
export async function login(
  params: LoginRequest
): Promise<LoginVo> {
  const loginVo = await post<LoginVo>('/auth/login', params, { flag: false, silent: true });
  return loginVo.data;
}

/**
* 获取登录用户信息
* @param params 登录参数
*/
export async function getUserInfo(): Promise<UserInfoVo> {
  const userInfoVo = await get<UserInfoVo>('/user/getUserInfo', { flag: true });
  return userInfoVo.data;
}

/**
* 退出登录
*/
export async function logouted(): Promise<any> {
  const logout = await get('/logout', { flag: true });
  return logout.data
}