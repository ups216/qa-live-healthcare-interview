import type { Doctor } from '../store';

const API_BASE = '/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export async function fetchDoctors(): Promise<Doctor[]> {
  const response = await fetch(`${API_BASE}/doctors`);
  return handleResponse<Doctor[]>(response);
}

export async function fetchDoctorByUsername(username: string): Promise<Doctor> {
  const response = await fetch(`${API_BASE}/doctors/username/${encodeURIComponent(username)}`);
  return handleResponse<Doctor>(response);
}

export async function loginDoctor(username: string, password: string): Promise<Doctor> {
  const response = await fetch(`${API_BASE}/doctors/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  return handleResponse<Doctor>(response);
}
