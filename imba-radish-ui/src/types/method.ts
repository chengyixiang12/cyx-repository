// src/types/api.ts

import { AxiosRequestConfig } from "axios";

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

// 定义类型
export interface RequestConfig extends AxiosRequestConfig {
  params?: Record<string, any>;
  flag?: boolean;
  silent?: boolean;
}