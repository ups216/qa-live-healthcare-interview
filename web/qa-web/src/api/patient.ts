import axios from 'axios';

const API_BASE = '/api/patients';

export interface PatientInfo {
  id: string;
  phone: string;
  name: string;
  gender?: number;
  birthday?: string;
  avatar?: string;
  status?: number;
  questionCount?: number;
  createdAt?: string;
}

export interface LoginResponse {
  patient: PatientInfo;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const patientApi = {
  async sendSmsCode(phone: string, type: string = 'register'): Promise<{ expiresIn: number }> {
    const response = await api.post<ApiResponse<{ expiresIn: number }>>('/sms/send', {
      phone,
      type,
    });
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },

  async register(data: {
    phone: string;
    code: string;
    password: string;
    name: string;
    gender?: number;
    birthday?: string;
  }): Promise<LoginResponse> {
    const response = await api.post<ApiResponse<LoginResponse>>('/register', data);
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },

  async login(phone: string, password: string): Promise<LoginResponse> {
    const response = await api.post<ApiResponse<LoginResponse>>('/login', { phone, password });
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },

  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response = await api.post<ApiResponse<LoginResponse>>('/token/refresh', {
      refreshToken,
    });
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },

  async logout(): Promise<void> {
    await api.post('/logout');
  },

  async getProfile(): Promise<PatientInfo> {
    const response = await api.get<ApiResponse<PatientInfo>>('/profile');
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },

  async checkPhone(phone: string): Promise<boolean> {
    const response = await api.get<ApiResponse<{ exists: boolean }>>('/check-phone', {
      params: { phone },
    });
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data.exists;
  },

  async upgrade(data: {
    phone: string;
    code: string;
    password: string;
    historyName: string;
    historyBirthday: string;
  }): Promise<{ linkedQuestions: number }> {
    const response = await api.post<ApiResponse<{ linkedQuestions: number }>>('/upgrade', data);
    if (response.data.code !== 0) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  },
};

export default api;
