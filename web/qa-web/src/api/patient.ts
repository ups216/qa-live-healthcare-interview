const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface ApiPatient {
  id: string;
  username: string;
  name: string;
  gender: string;
  birthday: string;
  phone?: string;
  active: boolean;
}

export interface ApiResponse<T> {
  data: T | null;
  total: number;
  error: string | null;
  message: string | null;
}

export interface UsernameCheckResult {
  available: boolean;
  username: string;
}

/**
 * 用户注册
 */
export async function registerPatient(data: {
  username: string;
  password: string;
  confirmPassword: string;
  name: string;
  gender: string;
  birthday: string;
  phone?: string;
}): Promise<ApiPatient> {
  const res = await fetch(`${BASE_URL}/api/patients/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const json: ApiResponse<ApiPatient> = await res.json();
  if (!res.ok) throw new Error(json.message || json.error || 'Registration failed');
  if (!json.data) throw new Error('Registration failed');
  return json.data;
}

/**
 * 用户登录
 */
export async function loginPatient(username: string, password: string): Promise<ApiPatient> {
  const res = await fetch(`${BASE_URL}/api/patients/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  const json: ApiResponse<ApiPatient> = await res.json();
  if (!res.ok) throw new Error(json.message || json.error || 'Login failed');
  if (!json.data) throw new Error('Login failed');
  return json.data;
}

/**
 * 退出登录
 */
export async function logoutPatient(): Promise<void> {
  await fetch(`${BASE_URL}/api/patients/logout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  });
}

/**
 * 检查用户名可用性
 */
export async function checkUsername(username: string): Promise<UsernameCheckResult> {
  const res = await fetch(
    `${BASE_URL}/api/patients/check-username?username=${encodeURIComponent(username)}`
  );
  const json: ApiResponse<UsernameCheckResult> = await res.json();
  if (!json.data) throw new Error('Failed to check username');
  return json.data;
}
