import { Doctor } from '../store';

const API_BASE_URL = 'http://localhost:8080/api';

// 通用请求方法
async function request<T>(endpoint: string, options?: RequestInit): Promise<T> {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// 医生相关API
export const doctorApi = {
  // 获取所有医生
  getAllDoctors: async (): Promise<Doctor[]> => {
    return request<Doctor[]>('/doctors');
  },

  // 多条件查询医生
  getDoctorsByConditions: async (params: {
    name?: string;
    department?: string;
    title?: string;
    isActive?: boolean;
  }): Promise<Doctor[]> => {
    const queryParams = new URLSearchParams();
    if (params.name) queryParams.append('name', params.name);
    if (params.department) queryParams.append('department', params.department);
    if (params.title) queryParams.append('title', params.title);
    if (params.isActive !== undefined) queryParams.append('isActive', params.isActive.toString());

    return request<Doctor[]>(`/doctors/conditions?${queryParams.toString()}`);
  },

  // 获取所有科室
  getAllDepartments: async (): Promise<string[]> => {
    return request<string[]>('/doctors/departments');
  },

  // 获取所有职称
  getAllTitles: async (): Promise<string[]> => {
    return request<string[]>('/doctors/titles');
  },

  // 根据ID获取医生
  getDoctorById: async (id: string): Promise<Doctor> => {
    return request<Doctor>(`/doctors/${id}`);
  },
};

export default {
  doctor: doctorApi,
};
