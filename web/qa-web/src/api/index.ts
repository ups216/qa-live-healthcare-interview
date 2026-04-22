const API_BASE_URL = '/api';

interface ApiResponse<T> {
  status: string;
  data?: T;
  message?: string;
  count?: number;
}

async function fetchApi<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API Error: ${response.status}`);
  }

  const data: ApiResponse<T> = await response.json();
  
  if (data.status === 'error') {
    throw new Error(data.message || 'Unknown error');
  }

  return data as T;
}

export interface Doctor {
  id: string;
  username: string;
  password: string;
  name: string;
  nameEn?: string;
  title: string;
  titleEn?: string;
  department: string;
  departmentEn?: string;
  avatar: string;
  experience: string;
  experienceEn?: string;
  specialties: string[];
  specialtiesEn?: string[];
  isActive: boolean;
}

export interface Patient {
  id: string;
  name: string;
  birthday: string;
  phone: string;
  gender: string;
  password?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Question {
  id: string;
  patientId: string;
  patientName: string;
  doctorId: string;
  doctorName: string;
  question: string;
  submitTime: string;
  status: 'pending' | 'answered';
  answer: string | null;
  answerTime: string | null;
}

function parseDoctorSpecialties(doctor: any): Doctor {
  return {
    ...doctor,
    specialties: typeof doctor.specialties === 'string' 
      ? JSON.parse(doctor.specialties) 
      : doctor.specialties,
    isActive: doctor.active !== undefined ? doctor.active : doctor.isActive
  };
}

export const doctorApi = {
  getAll: async (): Promise<Doctor[]> => {
    const response = await fetchApi<{ status: string; data: any[]; count: number }>(
      `${API_BASE_URL}/doctors`
    );
    return (response.data || []).map(parseDoctorSpecialties);
  },

  getActive: async (): Promise<Doctor[]> => {
    const response = await fetchApi<{ status: string; data: any[]; count: number }>(
      `${API_BASE_URL}/doctors/active`
    );
    return (response.data || []).map(parseDoctorSpecialties);
  },

  getById: async (id: string): Promise<Doctor | null> => {
    const response = await fetchApi<{ status: string; data: any }>(
      `${API_BASE_URL}/doctors/${id}`
    );
    return response.data ? parseDoctorSpecialties(response.data) : null;
  },

  getByUsername: async (username: string): Promise<Doctor | null> => {
    const response = await fetchApi<{ status: string; data: any }>(
      `${API_BASE_URL}/doctors/username/${username}`
    );
    return response.data ? parseDoctorSpecialties(response.data) : null;
  },
};

export const patientApi = {
  getAll: async (): Promise<Patient[]> => {
    const response = await fetchApi<{ status: string; data: Patient[]; count: number }>(
      `${API_BASE_URL}/patients`
    );
    return response.data || [];
  },

  getById: async (id: string): Promise<Patient | null> => {
    const response = await fetchApi<{ status: string; data: Patient }>(
      `${API_BASE_URL}/patients/${id}`
    );
    return response.data || null;
  },

  login: async (name: string, password: string): Promise<{ success: boolean; message: string; patient?: Patient }> => {
    try {
      // 调用qa-service-question项目的登录接口
      const response = await fetch('http://localhost:8081/api/patients/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, password }),
      });
      
      const data = await response.json();
      return {
        success: data.status === 'success',
        message: data.message,
        patient: data.status === 'success' ? { id: '', name: data.name, birthday: '', phone: '', gender: '' } : undefined,
      };
    } catch (error) {
      return {
        success: false,
        message: '登录失败，请稍后重试',
      };
    }
  },

  verify: async (name: string, birthday: string): Promise<{ success: boolean; message: string; patient?: Patient }> => {
    try {
      const response = await fetchApi<{ status: string; message: string; data?: Patient }>(
        `${API_BASE_URL}/patients/verify`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ name, birthday }),
        }
      );
      return {
        success: response.status === 'success',
        message: response.message,
        patient: response.data,
      };
    } catch (error) {
      return {
        success: false,
        message: '验证失败，请稍后重试',
      };
    }
  },

  resetPassword: async (name: string, birthday: string, newPassword: string): Promise<{ success: boolean; message: string }> => {
    try {
      const response = await fetchApi<{ status: string; message: string }>(
        `${API_BASE_URL}/patients/reset-password`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ name, birthday, newPassword }),
        }
      );
      return {
        success: response.status === 'success',
        message: response.message,
      };
    } catch (error) {
      return {
        success: false,
        message: '密码重置失败，请稍后重试',
      };
    }
  },

  changePassword: async (id: string, oldPassword: string, newPassword: string): Promise<{ success: boolean; message: string }> => {
    try {
      const response = await fetchApi<{ status: string; message: string }>(
        `${API_BASE_URL}/patients/change-password`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ id, oldPassword, newPassword }),
        }
      );
      return {
        success: response.status === 'success',
        message: response.message,
      };
    } catch (error) {
      return {
        success: false,
        message: '密码修改失败，请稍后重试',
      };
    }
  },
};

export const questionApi = {
  getAll: async (): Promise<Question[]> => {
    const response = await fetchApi<{ status: string; data: Question[]; count: number }>(
      `${API_BASE_URL}/questions`
    );
    return response.data || [];
  },

  getById: async (id: string): Promise<Question | null> => {
    const response = await fetchApi<{ status: string; data: Question }>(
      `${API_BASE_URL}/questions/${id}`
    );
    return response.data || null;
  },

  getByPatientId: async (patientId: string): Promise<Question[]> => {
    const response = await fetchApi<{ status: string; data: Question[]; count: number }>(
      `${API_BASE_URL}/questions/patient/${patientId}`
    );
    return response.data || [];
  },

  getByDoctorId: async (doctorId: string): Promise<Question[]> => {
    const response = await fetchApi<{ status: string; data: Question[]; count: number }>(
      `${API_BASE_URL}/questions/doctor/${doctorId}`
    );
    return response.data || [];
  },

  getByStatus: async (status: string): Promise<Question[]> => {
    const response = await fetchApi<{ status: string; data: Question[]; count: number }>(
      `${API_BASE_URL}/questions/status/${status}`
    );
    return response.data || [];
  },

  answer: async (id: string, answer: string): Promise<void> => {
    await fetchApi<{ status: string; message: string }>(
      `${API_BASE_URL}/questions/${id}/answer`,
      {
        method: 'POST',
        body: JSON.stringify({ answer }),
      }
    );
  },
};
