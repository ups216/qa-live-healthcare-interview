const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface ApiDoctor {
  id: string;
  username: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  active: boolean;
}

export interface ApiResponse<T> {
  data: T | null;
  total: number;
  error: string | null;
  message: string | null;
}

/**
 * 获取所有医生列表
 */
export async function getAllDoctors(): Promise<ApiDoctor[]> {
  const res = await fetch(`${BASE_URL}/api/doctors`);
  const json: ApiResponse<ApiDoctor[]> = await res.json();
  if (!res.ok) throw new Error(json.error || 'Failed to fetch doctors');
  return json.data || [];
}

/**
 * 根据ID获取医生详情
 */
export async function getDoctorById(id: string): Promise<ApiDoctor> {
  const res = await fetch(`${BASE_URL}/api/doctors/${id}`);
  const json: ApiResponse<ApiDoctor> = await res.json();
  if (!res.ok) throw new Error(json.error || 'Doctor not found');
  if (!json.data) throw new Error('Doctor not found');
  return json.data;
}
