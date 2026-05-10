import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json; charset=utf-8',
    'Accept': 'application/json; charset=utf-8',
  },
});

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  phone?: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: UserDTO;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export const patientApi = {
  // 患者登录
  login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post('/patients/login', credentials).then(response => response.data);
  },

  // 获取当前用户信息
  getCurrentUser(token: string): Promise<ApiResponse<UserDTO>> {
    return apiClient.get('/patients/me', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then(response => response.data);
  },
};
