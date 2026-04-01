import { ref } from 'vue';

// API基础URL
const API_BASE_URL = 'http://localhost:8081/api';

// API响应接口
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// 医生接口
interface Doctor {
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
}

// API服务类
export class ApiService {
  private static async request<T>(url: string, options: RequestInit = {}): Promise<T> {
    try {
      const response = await fetch(`${API_BASE_URL}${url}`, {
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
        ...options,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result: ApiResponse<T> = await response.json();
      
      if (!result.success) {
        throw new Error(result.message);
      }

      return result.data;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // 获取所有医生列表
  static async getDoctors(): Promise<Doctor[]> {
    return this.request<Doctor[]>('/doctors');
  }

  // 获取所有活跃的医生列表
  static async getActiveDoctors(): Promise<Doctor[]> {
    return this.request<Doctor[]>('/doctors/active');
  }

  // 根据ID获取医生详情
  static async getDoctorById(id: string): Promise<Doctor> {
    return this.request<Doctor>(`/doctors/${id}`);
  }

  // 根据科室获取医生列表
  static async getDoctorsByDepartment(department: string): Promise<Doctor[]> {
    return this.request<Doctor[]>(`/doctors/department/${encodeURIComponent(department)}`);
  }

  // 根据专业领域获取医生列表
  static async getDoctorsBySpecialty(specialty: string): Promise<Doctor[]> {
    return this.request<Doctor[]>(`/doctors/specialty/${encodeURIComponent(specialty)}`);
  }

  // 搜索医生
  static async searchDoctors(params: {
    name?: string;
    department?: string;
    title?: string;
  }): Promise<Doctor[]> {
    const queryParams = new URLSearchParams();
    if (params.name) queryParams.append('name', params.name);
    if (params.department) queryParams.append('department', params.department);
    if (params.title) queryParams.append('title', params.title);
    
    return this.request<Doctor[]>(`/doctors/search?${queryParams.toString()}`);
  }

  // 获取所有科室列表
  static async getDepartments(): Promise<string[]> {
    return this.request<string[]>('/doctors/departments');
  }

  // 获取所有专业领域列表
  static async getSpecialties(): Promise<string[]> {
    return this.request<string[]>('/doctors/specialties');
  }
}

// 响应式医生数据管理
export function useDoctors() {
  const doctors = ref<Doctor[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const loadDoctors = async () => {
    loading.value = true;
    error.value = null;
    try {
      doctors.value = await ApiService.getActiveDoctors();
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载医生数据失败';
      console.error('Failed to load doctors:', err);
    } finally {
      loading.value = false;
    }
  };

  const loadAllDoctors = async () => {
    loading.value = true;
    error.value = null;
    try {
      doctors.value = await ApiService.getDoctors();
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载医生数据失败';
      console.error('Failed to load doctors:', err);
    } finally {
      loading.value = false;
    }
  };

  return {
    doctors,
    loading,
    error,
    loadDoctors,
    loadAllDoctors,
  };
}