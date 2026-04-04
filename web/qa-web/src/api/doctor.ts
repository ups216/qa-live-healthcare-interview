const API_BASE_URL = 'http://localhost:8080/api';

export interface Doctor {
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

export async function getAllDoctors(locale: string = 'zh-CN'): Promise<Doctor[]> {
  const response = await fetch(`${API_BASE_URL}/doctors?locale=${locale}`);
  if (!response.ok) {
    throw new Error('Failed to fetch doctors');
  }
  return response.json();
}

export async function getActiveDoctors(locale: string = 'zh-CN'): Promise<Doctor[]> {
  const response = await fetch(`${API_BASE_URL}/doctors/active?locale=${locale}`);
  if (!response.ok) {
    throw new Error('Failed to fetch active doctors');
  }
  return response.json();
}

export async function getDoctorByUsername(username: string, locale: string = 'zh-CN'): Promise<Doctor | null> {
  const response = await fetch(`${API_BASE_URL}/doctors/${username}?locale=${locale}`);
  if (response.status === 404) {
    return null;
  }
  if (!response.ok) {
    throw new Error('Failed to fetch doctor');
  }
  return response.json();
}
