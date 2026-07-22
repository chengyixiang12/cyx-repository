import { PaginatedData } from '@/types/api';
import { GetSecretKeyListRequest, GenerateKeyRequest, SysSecretKeyVo } from '@/types/secretKey';
import { post } from '@/utils/http'

/**
 * 获取密钥列表
 */
export async function getSecretKeyListApi(data: GetSecretKeyListRequest): Promise<PaginatedData<SysSecretKeyVo>> {
  const res = await post<PaginatedData<SysSecretKeyVo>>('/secretKey/list', data, { flag: true });
  return res.data;
}

/**
 * 生成密钥对
 * @param params 生成密钥参数（type=用途类型, secretType=算法类型）
 */
export async function generateKeyApi(params: GenerateKeyRequest): Promise<void> {
  await post('/secretKey/generateKey', params, { flag: true });
}
