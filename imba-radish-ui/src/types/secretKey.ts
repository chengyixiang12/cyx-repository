export interface GetSecretKeyListRequest {
  pageNum: number;
  pageSize: number;
  type?: number | null;
}

export interface SysSecretKeyVo {
  id: string;
  type: number;
  publicKey: string;
  description: string;
  updateBy: string;
  updateTime: string;
}
