export interface GetSecretKeyListRequest {
  pageNum: number;
  pageSize: number;
  type?: number | null;
}

export interface GenerateKeyRequest {
  type: number;
  secretType: string;
  description?: string;
}

export const SECRET_TYPE_MAP: Record<string, string> = {
  '1': 'RSA',
  '2': 'AES'
}

export const SECRET_TYPE_OPTIONS = [
  { value: '1', label: 'RSA（非对称）' },
  { value: '2', label: 'AES（对称）' }
]

export interface SysSecretKeyVo {
  id: string;
  type: number;
  secretType: string;
  publicKey: string;
  privateKey: string;
  description: string;
  updateBy: string;
  updateTime: string;
}
