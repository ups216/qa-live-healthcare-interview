import type { Doctor } from '../store';

export async function fetchDoctors(): Promise<Doctor[]> {
  const res = await fetch('/api/doctors', {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
    credentials: 'omit',
  });

  if (!res.ok) {
    throw new Error(`Failed to fetch doctors: ${res.status}`);
  }

  const data = (await res.json()) as Doctor[];
  return Array.isArray(data) ? data : [];
}

