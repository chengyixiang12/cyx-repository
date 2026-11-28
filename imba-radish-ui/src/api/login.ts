import { LoginRequest, LoginVo } from '../types/login';
import { post, get, getBlob } from '@/utils/http';
import { generateShortUuid } from '@/utils/uuid';

export async function getGraphicCaptcha(uuid?: string): Promise<{ blob: Blob; uuid: string }> {
  const finalUKey = uuid || generateShortUuid();
  const blob = await getBlob<Blob>(
    '/auth/getGraphicCaptcha', { flag: false, params: { uuid: finalUKey }}
  );
  return { blob, uuid: finalUKey };
}

/**
 * 用户登录
 * @param data 登录参数
 * @param passEncode 加密密码
 */
export async function login(data: LoginRequest): Promise<LoginVo> {
  const res = await post<LoginVo>('/auth/login', data, { flag: false, silent: true });
  return res.data;
}

/**
* 退出登录
*/
export async function logouted(): Promise<any> {
  const res = await get('/logout', { flag: true });
  return res.data
}

/**
 * 发送登录验证码
 * @param email 
 */
export async function sendCaptchaApi(email: string): Promise<string> {
  const res = await get('/message/sendLoginCaptcha', { flag: false, params: { email } })
  return res.msg
}
/**
 * 发送忘记密码验证码
 * @param email
 */
export async function sendForgotPasswordCaptchaApi(email: string): Promise<string> {
  const res = await get('/message/sendForgotPasswordCaptcha', { flag: false, params: { email } })
  return res.msg
}

/**
 * 忘记密码-通过邮箱验证码重置密码
 * @param email 邮箱
 * @param verificationCode 邮箱验证码
 * @param password RSA 加密后的新密码
 */
export async function forgotPasswordApi(email: string, verificationCode: string, password: string): Promise<string> {
  const res = await post('/auth/forgotPassword', { email, verificationCode, password }, { flag: false, silent: true })
  return res.msg
}
