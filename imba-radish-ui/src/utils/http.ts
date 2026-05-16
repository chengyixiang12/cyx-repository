import axios, { AxiosResponse, Method } from 'axios';
import { ApiResponse, ApiError, RequestConfig } from '@/types/method';
import router from '@/router/routers';
import { clearCache } from '@/utils/clearCache';
import { showMessage } from './message';

const instance = axios.create({
  baseURL: '/api',
  timeout: 30000, // 延长超时时间
  headers: {
    'Content-Type': 'application/json' // 设置默认请求头
  }
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
  if (response.data && response.data.msg && response.data.msg !== '') {
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

// 核心请求方法
async function request<T = any>(
  method: Method,
  url: string,
  config?: any,
): Promise<any> {
  try {
    // 1. 解构配置：分离 业务配置(silent/flag)、axios 核心配置(data/params/headers等)
    const { 
      silent, 
      flag, // 单独解构 flag，不混入 axiosConfig
      ...axiosConfig 
    } = config || {};

    // 2. 构建最终的 axios 配置：明确区分 data（请求体）、params（URL参数）
    const finalConfig = {
      method,
      url,
      // 核心：仅保留 axios 标准配置（避免业务配置污染）
      data: axiosConfig.data, // 请求体（POST/PUT/DELETE 用）
      params: axiosConfig.params, // URL 查询参数（GET/DELETE 用）
      headers: {
        ...instance.defaults.headers, // 实例默认头（如 Content-Type: application/json）
        ...axiosConfig.headers, // 自定义 headers
        ...(flag !== undefined && { 'X-Flag': String(flag) }), // 仅处理 flag 到 headers
      },
      // 其他 axios 标准配置（如 responseType/timeout 等）
      responseType: axiosConfig.responseType,
      timeout: axiosConfig.timeout,
    };

    // 3. 发送请求
    const response = await instance.request(finalConfig);

    // 4. 二进制响应处理（保留你的原有逻辑）
    if (finalConfig.responseType === 'blob' || finalConfig.responseType === 'arraybuffer') {
      if (!(response.data instanceof Blob)) {
        throw new Error(`Expected Blob but got ${typeof response.data}`);
      }
      return response.data;
    }

    // 5. JSON 响应处理（保留你的原有逻辑）
    const res = response.data as ApiResponse<T>;
    if (res.code && res.code !== 2001) throw new Error(res.msg);
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

export async function post<T = any>(
  url: string,
  data?: any,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return request('POST', url, { ...config, data });
}

export async function put<T = any>(
  url: string,
  data?: any,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return request('PUT', url, { ...config, data });
}

export async function del<T = any>(
  url: string,
  data?: any,
  config?: RequestConfig
): Promise<ApiResponse<T>> {
  return request<T>('DELETE', url, {
    ...config,
    data: data
  });
}

// 获取二进制流
export async function getBlob<Blob>(
  url: string,
  config?: Omit<RequestConfig, 'responseType'>
): Promise<Blob> {
  const response = await instance.get<Blob>(url, { 
    ...config, 
    responseType: 'blob' 
  });
  return response.data;
}

// 获取二进制流
export async function postBlob<Blob>(
  url: string,
  data?: any,
  config?: Omit<RequestConfig, 'responseType'>
): Promise<{ blob: Blob; filename: string }> {
  const response = await instance.post(url, data, {
    ...config,
    responseType: 'blob'
  })

  const contentDisposition = response.headers['content-disposition']
  let filename = '下载文件'

  if (contentDisposition) {
    const match = contentDisposition.match(/filename="?([^"]+)"?/)
    if (match && match[1]) {
      filename = decodeURIComponent(match[1])
    }
  }

  return {
    blob: response.data,
    filename
  }
}

// Actuator专用请求方法
export async function getActuator<Metrics>(endpoint: string): Promise<Metrics> {
  endpoint = `/actuator${endpoint}`
  const response = await instance.get<Metrics>(endpoint)
  return response.data;
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