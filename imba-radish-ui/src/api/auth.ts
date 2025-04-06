import { get } from '@/utils/http';
import type { PublicKeyVo } from '../types/auth';

/**
 * 获取加密公钥
 * @param type
 */
export async function getPublicKey(
  type: number
): Promise<string> {
  const res = await get<PublicKeyVo>('/secretKey/getPublicKey', { flag: false, params: { type } });
  return res.data.publicKey;
}