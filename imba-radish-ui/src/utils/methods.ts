// src/utils/method.ts
import instance from './http';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import type { ApiResponse } from '../types/method'; // 假设已定义后端响应类型
import router from '@/router/routers';
import { showNotify } from '../utils/notify';
import { clearCache } from '../utils/clearCache';

/**
 * 核心请求方法（私有）
 */
async function _request<T>(
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  url: string,
  config?: {
    params?: any;
    data?: any;
    flag?: boolean;
    responseType?: AxiosRequestConfig['responseType'];
  }
): Promise<T> {
  try {
    const headers = config?.flag !== undefined ? { flag: config.flag } : undefined;
    
    const response: AxiosResponse<ApiResponse<T>> = await instance({
      method,
      url,
      params: config?.params,
      data: config?.data,
      headers,
      responseType: config?.responseType,
    });

    // 处理二进制响应（如图片验证码）
    if (config?.responseType && config.responseType !== 'json') {
      return response.data as unknown as T;
    }

    // 处理业务错误（假设2001表示成功）
    if (response.data.code !== 2001) {
      // throw new Error(response.data.msg || '请求失败');
      exceCode(response.data)
      throw new Error(response.data.msg || '请求失败');
    }

    return response.data.data;
  } catch (error) {
    console.error(`[API Error] ${method} ${url}:`, error);
    throw error;
  }
}

// 服务端返回错误信息码时，提示错误信息
function exceCode(data: ApiResponse) {
  const msg = data?.msg;
  switch (data?.code) {
    case 5001: {
      // 服务异常
      showNotify(msg, 'error');
      break;
    }
    case 5002: {
      // 认证过期，请重新登录
      showNotify(msg, 'error');
      clearCache()
      router.push('/login');
      break;
    }
    case 5003: {
      // 权限不足
      showNotify(msg, 'error');
      break;
    }
    case 5004: {
      // 认证失败，请重新登录
      showNotify(msg, 'error');
      clearCache()
      router.push('/login');
      break;
    }
    case 5005: {
      // 认证失败，请重新登录
      showNotify(msg, 'error');
      clearCache()
      router.push('/login');
      break;
    }
    case 5006: {
      // 系统繁忙，请稍后再试
      showNotify(msg, 'error');
      break;
    }
  }
}

// =============== 对外暴露的方法 =============== //

export function get<T = any>(
  url: string,
  flag: boolean,
  params?: any,
  responseType?: 'json' | 'arraybuffer' | 'blob'
): Promise<T> {
  return _request<T>('GET', url, { params, flag, responseType });
}

export function post<T = any>(
  url: string,
  flag: boolean,
  data?: any,
  params?: any
): Promise<T> {
  return _request<T>('POST', url, { data, params, flag });
}

export function put<T = any>(
  url: string,
  data?: any,
  flag: boolean = false // 默认值
): Promise<T> {
  return _request<T>('PUT', url, { data, flag });
}

export function del<T = any>(
  url: string,
  flag: boolean = false
): Promise<T> {
  return _request<T>('DELETE', url, { flag });
}