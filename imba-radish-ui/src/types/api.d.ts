/**
 * 基础API响应结构
 */
export interface ApiResponse<T = any> {
  timestamp: number;
  code: number;
  msg: string;
  data: T;
}

/**
 * 分页数据结构
 */
export interface PaginatedData<T> {
  records: T[];
  total: number;
}

/**
 * 分页API响应
 */
export interface PaginatedResponse<T> extends ApiResponse<PaginatedData<T>> { }

/**
 * 错误响应结构
 */
export interface ApiError {
  message: string;
  response?: {
    data?: ApiResponse;
    status?: number;
  };
  config?: AxiosRequestConfig;
}