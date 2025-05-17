import { RegisterRequest } from '@/types/register'
import { get, post } from '@/utils/http'

/**
 * 发送注册验证码
 * @param email 邮箱
 */
export async function sendRegistCaptchaApi(email: string | null) {
    await get('/message/sendRegistCaptcha', { flag: false, params: { email } })
}

/**
 * 
 * @param data 
 */
export async function registerApi(data: RegisterRequest) {
    await post('/auth/register', data, { flag: false })
}