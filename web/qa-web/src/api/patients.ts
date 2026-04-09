import type { Patient } from '../types/patient';

const TOKEN_KEY = 'qa_patient_token';

export function getPatientToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setPatientToken(token: string | null): void {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
  } else {
    localStorage.removeItem(TOKEN_KEY);
  }
}

export interface PatientLoginResponse {
  token: string;
  expiresIn: number;
  patient: Patient;
}

async function parseErrorMessage(res: Response): Promise<string | undefined> {
  try {
    const text = await res.text();
    if (!text) return undefined;
    const j = JSON.parse(text) as { message?: string; error?: string };
    return j.message ?? j.error;
  } catch {
    return undefined;
  }
}

function mapAuthError(status: number, code: string | undefined): string {
  if (status === 401) {
    return '用户名或密码错误';
  }
  if (status === 409 || code === 'USERNAME_TAKEN') {
    return '该用户名已被注册';
  }
  if (status === 400) {
    return '请检查输入内容';
  }
  return '操作失败，请稍后重试';
}

export async function loginPatientApi(
  username: string,
  password: string
): Promise<PatientLoginResponse> {
  const res = await fetch('/api/patients/login', {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username: username.trim(), password }),
  });
  if (!res.ok) {
    const code = await parseErrorMessage(res);
    throw new Error(mapAuthError(res.status, code));
  }
  return res.json() as Promise<PatientLoginResponse>;
}

export interface RegisterPatientPayload {
  username: string;
  password: string;
  name: string;
  birthday?: string;
  phone?: string;
  gender?: string;
}

export async function registerPatientApi(
  payload: RegisterPatientPayload
): Promise<PatientLoginResponse> {
  const res = await fetch('/api/patients/register', {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      username: payload.username.trim(),
      password: payload.password,
      name: payload.name.trim(),
      birthday: payload.birthday?.trim() || undefined,
      phone: payload.phone?.trim() || undefined,
      gender: payload.gender?.trim() || undefined,
    }),
  });
  if (!res.ok) {
    const code = await parseErrorMessage(res);
    throw new Error(mapAuthError(res.status, code));
  }
  return res.json() as Promise<PatientLoginResponse>;
}

export async function fetchCurrentPatient(token: string): Promise<Patient> {
  const res = await fetch('/api/patients/me', {
    method: 'GET',
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${token}`,
    },
  });
  if (!res.ok) {
    const code = await parseErrorMessage(res);
    throw new Error(code ?? '会话已失效，请重新登录');
  }
  return res.json() as Promise<Patient>;
}
