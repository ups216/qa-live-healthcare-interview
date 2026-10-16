import axios, { AxiosError } from 'axios';

/**
 * 后端统一响应结构，对应服务端 common.ApiResponse。
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

/**
 * axios 实例。
 *
 * baseURL 使用同源的 '/api'，由 vite.config.ts 的 server.proxy 转发到后端 qa-service-user:8080，
 * 因此开发期不存在跨域问题，部署时只需调整代理目标而无需改动业务代码。
 *
 * 响应拦截器负责"剥壳"：把 ApiResponse.data 直接交给调用方，
 * 业务代码不必重复判断 code、也不必写 `res.data.data` 这种双重解包。
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/** 本地 token 的 storage key，需求3 的登录态使用 */
export const TOKEN_KEY = 'qa-live-token';

/**
 * 登录态失效事件名。
 * api 层不直接依赖 router，改为广播事件，由上层的路由/App 统一响应，
 * 避免 api → router → store → api 的循环引用与多处重复跳转逻辑。
 */
export const AUTH_EXPIRED_EVENT = 'qa-live-auth-expired';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
  window.dispatchEvent(new Event(AUTH_EXPIRED_EVENT));
}

function isApiResponse(value: unknown): value is ApiResponse<unknown> {
  return (
    typeof value === 'object' &&
    value !== null &&
    'code' in value &&
    'data' in value
  );
}

// ---------- 请求拦截器：注入 Bearer token ----------
request.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ---------- 响应拦截器：剥壳 + 业务失败 + 401 ----------
request.interceptors.response.use(
  (response) => {
    const body: unknown = response.data;

    if (isApiResponse(body)) {
      if (body.code === 0) {
        // 解包：把 ApiResponse.data 提升为响应体，调用方直接拿到 payload
        response.data = body.data;
        return response;
      }
      // 业务失败（非 0 code）：转成 reject 并带上服务端 message
      return Promise.reject(new Error(body.message || '请求失败'));
    }

    // 非 ApiResponse 结构（例如既有 /api/test/cors 返回裸 Map）：原样透传
    return response;
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response?.status === 401) {
      // token 缺失、过期或被篡改：清理本地凭证并广播事件，
      // 由上层决定跳转，避免在拦截器里直接 router.push 造成跳转循环。
      clearToken();
    }
    const serverMessage = error.response?.data?.message;
    return Promise.reject(new Error(serverMessage || error.message || '网络异常'));
  }
);

export default request;
