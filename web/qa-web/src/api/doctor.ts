import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json; charset=utf-8',
    'Accept': 'application/json; charset=utf-8',
  },
  responseType: 'json',
});

export interface DoctorDTO {
  id: string;
  username: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
}

export const doctorApi = {
  // 获取所有医生列表
  getAllDoctors(): Promise<DoctorDTO[]> {
    return apiClient.get('/doctors').then(response => response.data);
  },

  // 获取在线医生列表
  getActiveDoctors(): Promise<DoctorDTO[]> {
    return apiClient.get('/doctors/active').then(response => response.data);
  },

  // 根据ID获取医生信息
  getDoctorById(id: string): Promise<DoctorDTO> {
    return apiClient.get(`/doctors/${id}`).then(response => response.data);
  },

  // 根据用户名获取医生信息
  getDoctorByUsername(username: string): Promise<DoctorDTO> {
    return apiClient.get(`/doctors/username/${username}`).then(response => response.data);
  },
};
