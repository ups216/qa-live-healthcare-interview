import { API_CONFIG, API_ENDPOINTS } from '../config/api';

// API响应接口
export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  total?: number;
}

// 医生接口
export interface Doctor {
  id: string;
  username: string;
  password: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// 登录请求接口
export interface LoginRequest {
  username: string;
  password: string;
}

class ApiService {
  private baseUrl: string;

  constructor() {
    this.baseUrl = API_CONFIG.BASE_URL;
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
    const url = `${this.baseUrl}${endpoint}`;
    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data as ApiResponse<T>;
    } catch (error) {
      console.error('API request failed:', error);
      return {
        success: false,
        message: error instanceof Error ? error.message : '网络请求失败',
      };
    }
  }

  // 获取所有医生
  async getAllDoctors(): Promise<ApiResponse<Doctor[]>> {
    return this.request<Doctor[]>(API_ENDPOINTS.DOCTORS.BASE);
  }

  // 获取所有活跃的医生
  async getActiveDoctors(): Promise<ApiResponse<Doctor[]>> {
    return this.request<Doctor[]>(API_ENDPOINTS.DOCTORS.ACTIVE);
  }

  // 根据ID获取医生
  async getDoctorById(id: string): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.BY_ID(id));
  }

  // 根据用户名获取医生
  async getDoctorByUsername(username: string): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.BY_USERNAME(username));
  }

  // 根据科室获取医生
  async getDoctorsByDepartment(department: string): Promise<ApiResponse<Doctor[]>> {
    return this.request<Doctor[]>(API_ENDPOINTS.DOCTORS.BY_DEPARTMENT(department));
  }

  // 根据科室获取活跃的医生
  async getActiveDoctorsByDepartment(department: string): Promise<ApiResponse<Doctor[]>> {
    return this.request<Doctor[]>(API_ENDPOINTS.DOCTORS.ACTIVE_BY_DEPARTMENT(department));
  }

  // 医生登录
  async login(credentials: LoginRequest): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.LOGIN, {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  }

  // 创建医生
  async createDoctor(doctor: Omit<Doctor, 'id' | 'createdAt' | 'updatedAt'>): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.BASE, {
      method: 'POST',
      body: JSON.stringify(doctor),
    });
  }

  // 更新医生
  async updateDoctor(id: string, doctor: Partial<Doctor>): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.BY_ID(id), {
      method: 'PUT',
      body: JSON.stringify(doctor),
    });
  }

  // 删除医生
  async deleteDoctor(id: string): Promise<ApiResponse> {
    return this.request(API_ENDPOINTS.DOCTORS.BY_ID(id), {
      method: 'DELETE',
    });
  }

  // 更新医生状态
  async updateDoctorStatus(id: string, isActive: boolean): Promise<ApiResponse<Doctor>> {
    return this.request<Doctor>(API_ENDPOINTS.DOCTORS.STATUS(id), {
      method: 'PATCH',
      body: JSON.stringify({ isActive }),
    });
  }

  // 用户登录
  async loginUser(username: string, password: string): Promise<ApiResponse<any>> {
    return this.request<any>(API_ENDPOINTS.USERS.LOGIN, {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
  }

  // 用户注册
  async registerUser(username: string, password: string, name: string, email?: string, phone?: string): Promise<ApiResponse<any>> {
    return this.request<any>(API_ENDPOINTS.USERS.REGISTER, {
      method: 'POST',
      body: JSON.stringify({ username, password, name, email, phone }),
    });
  }

  // 根据用户名获取用户信息
  async getUserByUsername(username: string): Promise<ApiResponse<any>> {
    return this.request<any>(API_ENDPOINTS.USERS.BY_USERNAME(username));
  }
}

export const apiService = new ApiService();