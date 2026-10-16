import request from './request';
import { setToken, clearToken, getToken } from './request';

/** 患者脱敏信息，对应后端 dto/PatientVO（不含密码） */
export interface PatientVO {
  id: string;
  username: string;
  name: string;
  birthday: string | null;
  phone: string | null;
  gender: string | null;
}

/** 登录成功响应 */
export interface LoginResult {
  token: string;
  expiresIn: number;
  patient: PatientVO;
}

/** 注册入参 */
export interface RegisterPayload {
  username: string;
  password: string;
  name: string;
  birthday?: string;
  phone?: string;
  gender?: string;
}

/** 注册：成功返回脱敏用户信息 */
export function register(payload: RegisterPayload): Promise<PatientVO> {
  return request.post<PatientVO>('/patient/register', payload).then((res) => res.data);
}

/** 登录：成功后由调用方决定 token 的存放时机 */
export function login(username: string, password: string): Promise<LoginResult> {
  return request
    .post<LoginResult>('/patient/login', { username, password })
    .then((res) => res.data);
}

/** 获取当前登录患者（需 Bearer token），失败返回 null */
export async function fetchMe(): Promise<PatientVO | null> {
  try {
    const res = await request.get<PatientVO>('/patient/me');
    return res.data;
  } catch {
    return null;
  }
}

/** 退出：清理本地 token（服务端 JWT 无状态，无需注销接口） */
export function logout(): void {
  clearToken();
}

export { setToken, clearToken, getToken };
