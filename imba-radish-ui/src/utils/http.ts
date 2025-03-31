// src/utils/http.ts
import axios, { AxiosRequestConfig, Method } from 'axios';
import { ApiResponse, ApiError } from '../types/method';

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('Authorization');
    if (token && config.headers) {
      const requiresAuth = config.headers['flag'] !== false;
      if (requiresAuth) {
        config.headers['Authorization'] = token;
      }
    }
    if (config.headers) {
      delete config.headers['flag'];
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    // 转换错误格式
    const apiError: ApiError = {
      ...error,
      message: error.response?.data?.msg || error.message,
      response: {
        data: error.response?.data,
        status: error.response?.status,
      },
    };
    return Promise.reject(apiError);
  }
);

/**
 * 核心请求方法
 * @param method 请求方法
 * @param url 请求地址
 * @param config 配置
 */
async function request<T = any>(
  method: Method,
  url: string,
  config?: AxiosRequestConfig & { 
    flag?: boolean; 
    silent?: boolean; // 是否静默处理错误
  }
): Promise<T> {
  try {
    const response = await instance.request<ApiResponse<T>>({
      url,
      method,
      ...config,
      headers: {
        ...config?.headers,
        flag: config?.flag,
      },
    });
    // 二进制响应直接返回
    if (config?.responseType && config.responseType !== 'json') {
      return response.data as unknown as T;
    }

    // 处理业务错误（根据你们的code规范）
    const res = response.data;
    if (res.code !== 2001) { // 假设2001表示成功
      throw {
        message: res.msg,
        response: { data: res },
      } as ApiError;
    }

    return res.data;
  } catch (error) {
    return handleError(error as ApiError, config?.silent);
  }
}

// 统一错误处理
function handleError(error: ApiError, silent = false): never {
  const errorMessage = error.response?.data?.msg || error.message;
  !silent && console.error(`[API Error] ${error.config?.url}:`, {
    code: error.response?.data?.code,
    message: errorMessage,
  });

  throw new Error(errorMessage);
}

// ================= 导出常用方法 ================= //
export function get<T = any>(
  url: string,
  config?: {
    params?: Record<string, any>;
    flag?: boolean;
    silent?: boolean;
    responseType?: 'arraybuffer' | 'blob';
  }
): Promise<T> {
  return request('GET', url, config);
}

export function post<T = any>(
  url: string,
  data?: any,
  config?: {
    params?: Record<string, any>;
    flag?: boolean;
    silent?: boolean;
  }
): Promise<T> {
  return request('POST', url, { ...config, data });
}

// 其他方法同理...
// export const put = /* 类似post */;
// export const del = /* 类似get */;

export default instance;