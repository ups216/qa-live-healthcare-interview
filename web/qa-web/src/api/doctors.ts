import type { Doctor } from '../store';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function getDoctors(): Promise<Doctor[]> {
  const response = await fetch(`${API_BASE_URL}/api/doctors`);
  if (!response.ok) {
    throw new Error(`Failed to load doctors: ${response.status}`);
  }

  const data = await response.json();

  return data.map((item: any) => ({
    id: item.id,
    username: item.username,
    password: item.password,
    name: item.name,
    title: item.title,
    department: item.department,
    avatar: item.avatar,
    experience: item.experience,
    specialties: Array.isArray(item.specialties)
      ? item.specialties
      : typeof item.specialties === 'string'
        ? JSON.parse(item.specialties || '[]')
        : [],
    isActive: item.active ?? item.is_active ?? true,
  })) as Doctor[];
}

