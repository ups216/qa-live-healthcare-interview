// API配置
export const API_CONFIG = {
  BASE_URL: 'http://localhost:8081/api',
  TIMEOUT: 10000,
} as const;

// API端点
export const API_ENDPOINTS = {
  DOCTORS: {
    BASE: '/doctors/',
    ACTIVE: '/doctors/active',
    BY_ID: (id: string) => `/doctors/${id}`,
    BY_USERNAME: (username: string) => `/doctors/username/${username}`,
    BY_DEPARTMENT: (department: string) => `/doctors/department/${department}`,
    ACTIVE_BY_DEPARTMENT: (department: string) => `/doctors/department/${department}/active`,
    LOGIN: '/doctors/login',
    STATUS: (id: string) => `/doctors/${id}/status`,
  },
  USERS: {
    LOGIN: '/users/login',
    REGISTER: '/users/register',
    BY_USERNAME: (username: string) => `/users/${username}`,
  },
} as const;