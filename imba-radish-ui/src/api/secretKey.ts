import { PaginatedData } from '@/types/api';
import { GetSecretKeyListRequest, SysSecretKeyVo } from '@/types/secretKey';
import { get, post } from '@/utils/http'

/**
 * 获取密钥列表
 */
export async function getSecretKeyListApi(data: GetSecretKeyListRequest): Promise<PaginatedData<SysSecretKeyVo>> {
  const res = await post<PaginatedData<SysSecretKeyVo>>('/secretKey/list', data, { flag: true });
  return res.data;
}

/**
 * 生成密钥对
 * @param type 密钥类型
 */
export async function generateKeyApi(type: number): Promise<void> {
  await get('/secretKey/generateKey', { flag: true, params: { type } });
}
