import axios, { AxiosRequestConfig, AxiosResponse, Method } from 'axios';
import { ApiResponse, ApiError, RequestConfig } from '@/types/method';
import router from '@/router/routers';
import { clearCache } from '@/utils/clearCache';
import { showMessage } from './message';

const instance = axios.create({
  baseURL: '/api',
  timeout: 30000, // 延长超时时间
});

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 处理认证令牌
    const token = sessionStorage.getItem('Authorization');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`; // 标准化Bearer Token
    }

    // 处理flag标记（仅作为header）
    if (config.headers?.flag !== undefined) {
      config.headers['X-Flag'] = config.headers.flag; // 重命名为更标准的X-Flag
      delete config.headers.flag;
    }

    // 确保JSON请求头
    if (!config.headers?.['Content-Type'] && typeof config.data === 'object') {
      // 使用 Axios 的标准方式设置 headers
      config.headers = axios.AxiosHeaders.from(config.headers);
      config.headers.set('Content-Type', 'application/json');
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => response.config.responseType !== 'blob' ? parseResponse(response) : response,
  (error) => handleResponseError(error)
);

// 响应数据处理
function parseResponse<T>(response: AxiosResponse<ApiResponse<T>>) {
  if (response.data.msg !== '') {
    showCustomMessage(response.data.msg, response.data.code);
  }
  return response;
}

// 错误统一处理
function handleResponseError(error: any) {
  const apiError: ApiError = {
    message: error.response?.data?.msg || error.message,
    response: {
      data: error.response?.data,
      status: error.response?.status,
    },
    config: error.config,
    name: ''
  };

  if (error.response?.data?.code) {
    handleErrorCode(error.response.data);
  }

  return Promise.reject(apiError);
}

// 错误码处理
function handleErrorCode(data: ApiResponse) {
  const authErrorCodes = [5002, 5004, 5005];
  if (authErrorCodes.includes(data.code)) {
    clearCache();
    router.push('/login');
  } else if (data.code === 5003) {
    router.push('/403');
  }
  showCustomMessage(data.msg, data.code)
}

// 核心请求方法（重载版本）
async function request<T = any>(
  method: Method,
  url: string,
  config?: AxiosRequestConfig & {
    flag?: boolean;
    silent?: boolean;
  }
): Promise<ApiResponse<T>>;

async function request(
  method: Method,
  url: string,
  config: AxiosRequestConfig & {
    flag?: boolean;
    silent?: boolean;
    responseType: 'blob' | 'arraybuffer';
  }
): Promise<Blob>;

async function request<T = any>(
  method: Method,
  url: string,
  config?: any
): Promise<any> {
  try {
    const { silent, ...axiosConfig } = config || {};
    const response = await instance.request({
      method,
      url,
      ...axiosConfig,
      headers: {
        ...axiosConfig?.headers,
        ...(config?.flag !== undefined && { 'X-Flag': String(config.flag) }),
      },
    });

    // 二进制响应
    if (config?.responseType === 'blob' || config?.responseType === 'arraybuffer') {
      if (!(response.data instanceof Blob)) {
        throw new Error(`Expected Blob but got ${typeof response.data}`);
      }
      return response.data;
    }

    // JSON 响应
    const res = response.data as ApiResponse<T>;
    if (res.code !== 2001) throw new Error(res.msg);
    return res;

  } catch (error) {
    return handleRequestError(error as ApiError, config?.silent);
  }
}

// 请求错误处理
function handleRequestError(error: ApiError, silent = false): never {
  if (!silent && error.message) {
    console.error(`[API Error] ${error.config?.url || ''}:`, error.message);
  }
  throw error;
}

// ================= 方法导出 ================= //
// 获取完整响应
export async function get<T = any>(
  url: string,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return request('GET', url, config);
}

// 获取纯数据（自动解构）
export async function getData<T = any>(
  url: string,
  config?: RequestConfig
): Promise<T> {
  return get<T>(url, config).then(res => res.data);
}

// 获取二进制流
export async function getBlob(
  url: string,
  config?: Omit<RequestConfig, 'responseType'>
): Promise<Blob> {
  const response = await instance.get<Blob>(url, { 
    ...config, 
    responseType: 'blob' 
  });
  return response.data;
}

export async function post<T = any>(
  url: string,
  data?: any,
  config?: {
    params?: Record<string, any>;
    flag?: boolean;
    silent?: boolean;
  }
): Promise<ApiResponse<T>> {
  return request('POST', url, { ...config, data });
}

export async function put<T = any>(
  url: string,
  data?: any,
  config?: {
    params?: Record<string, any>;
    flag?: boolean;
    silent?: boolean;
  }
): Promise<ApiResponse<T>> {
  return request('PUT', url, { ...config, data });
}

export async function del<T = any>(
  url: string,
  config?: {
    params?: Record<string, any>;
    data?: any;
    flag?: boolean;
    silent?: boolean;
  }
): Promise<ApiResponse<T>> {
  return request('DELETE', url, config);
}

/**
 * 自定义位置和颜色的消息提示
 * @param {string} message 提示消息内容
 * @param {number} code 状态码
 */
function showCustomMessage(message: string, code: number) {
  showMessage(message, code === 2001 ? 'success' : 'error')
}

export default instance;