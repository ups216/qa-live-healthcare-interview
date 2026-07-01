import type { Patient } from '../store';

const API_BASE = '/api';

export interface PatientRegisterPayload {
  username: string;
  password: string;
  name: string;
  birthday?: string;
  phone?: string;
  gender?: string;
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export async function loginPatient(username: string, password: string): Promise<Patient> {
  const response = await fetch(`${API_BASE}/patients/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  return handleResponse<Patient>(response);
}

export async function registerPatient(payload: PatientRegisterPayload): Promise<Patient> {
  const response = await fetch(`${API_BASE}/patients/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return handleResponse<Patient>(response);
}
