// src/types/api.ts
/**
 * 后端响应结构
 * @param T 数据类型
 */
export interface ApiResponse<T = any> {
    timestamp: number;
    code: number;       // 如2001
    msg: string;        // 如"成功"
    data: T;            // 实际业务数据
  }
  
  /**
   * 分页数据结构
   * @param T 列表项类型
   */
  export interface ApiPagination<T = any> {
    list: T[];
    total: number;
    page: number;
    size: number;
  }
  
  /**
   * 错误类型扩展
   */
  export interface ApiError extends Error {
    response?: {
      data?: ApiResponse;
      status?: number;
    };
    config?: {
      url?: string;
    };
  }