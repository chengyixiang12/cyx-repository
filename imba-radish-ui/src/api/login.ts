import { LoginRequest, LoginVo, UserInfoVo } from '../types/login';
import { post, get } from '@/utils/methods';
import { v4 as uuidv4 } from 'uuid';

export async function getGraphicCaptcha(uuid?: string): Promise<{ blob: Blob; uuid: string }> {
   // 生成或使用传入的唯一标识
   const finalUKey = uuid || uuidv4();
   // 调用method.ts的get方法（明确指定responseType为'blob'）
  const blob = await get<Blob>(
   `/auth/getGraphicCaptcha/${finalUKey}`,
   false, // 不需要认证
   null,  // 无查询参数
   'blob' // 指定返回Blob类型
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
   // 调用method.ts封装的post方法
   // 第二个参数false表示不需要携带认证token
   return post<LoginVo>('/auth/login', false, params);
 }

 /**
 * 获取登录用户信息
 * @param params 登录参数
 */
export async function getUserInfo(): Promise<UserInfoVo> {
  // 调用method.ts封装的post方法
  // 第二个参数false表示不需要携带认证token
  return get<UserInfoVo>('/user/getUserInfo', true, null);
}

 /**
 * 退出登录
 */
 export async function logouted(): Promise<UserInfoVo> {
  // 调用method.ts封装的post方法
  // 第二个参数false表示不需要携带认证token
  return get('/logout', true, null);
}