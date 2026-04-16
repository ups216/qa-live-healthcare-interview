import { reactive } from 'vue';

const USER_SERVICE_BASE_URL = import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8080';
const QUESTION_SERVICE_BASE_URL = import.meta.env.VITE_QUESTION_SERVICE_URL || 'http://localhost:8081';
const CURRENT_DOCTOR_KEY = 'qa_current_doctor';
const CURRENT_PATIENT_KEY = 'qa_current_patient';

export interface Doctor {
  id: string;
  username: string;
  password?: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
}

export interface Patient {
  id: string;
  username: string;
  displayName: string;
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

export interface DoctorLoginPayload {
  username: string;
  password: string;
}

interface State {
  doctors: Doctor[];
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

const state = reactive<State>({
  doctors: [],
  questions: [],
  currentDoctor: null,
  currentPatient: null,
});

const mapDoctor = (doctor: any): Doctor => ({
  ...doctor,
  isActive: doctor.isActive,
});

const mapQuestion = (question: any): Question => ({
  ...question,
  status: question.status as 'pending' | 'answered',
});

const readStorage = <T>(key: string): T | null => {
  if (typeof window === 'undefined') {
    return null;
  }
  const value = window.localStorage.getItem(key);
  if (!value) {
    return null;
  }
  try {
    return JSON.parse(value) as T;
  } catch {
    window.localStorage.removeItem(key);
    return null;
  }
};

const writeStorage = (key: string, value: unknown) => {
  if (typeof window === 'undefined') {
    return;
  }
  window.localStorage.setItem(key, JSON.stringify(value));
};

const removeStorage = (key: string) => {
  if (typeof window === 'undefined') {
    return;
  }
  window.localStorage.removeItem(key);
};

export const store = {
  state,

  async initializeSession(): Promise<void> {
    const savedDoctor = readStorage<Doctor>(CURRENT_DOCTOR_KEY);
    const savedPatient = readStorage<Patient>(CURRENT_PATIENT_KEY);

    if (savedDoctor) {
      state.currentDoctor = savedDoctor;
      state.currentPatient = null;
      await this.loadQuestions({ doctorId: savedDoctor.id });
      return;
    }

    if (savedPatient) {
      state.currentPatient = savedPatient;
      state.currentDoctor = null;
      await this.loadQuestions({ patientId: savedPatient.id });
    }
  },

  async loadDoctors(activeOnly = false): Promise<Doctor[]> {
    const url = `${USER_SERVICE_BASE_URL}/api/doctors${activeOnly ? '?activeOnly=true' : ''}`;
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Failed to load doctors');
    }
    const doctors = (await response.json()).map(mapDoctor);
    if (!activeOnly) {
      state.doctors = doctors;
    }
    return doctors;
  },

  async loginDoctor(payload: DoctorLoginPayload): Promise<Doctor> {
    const response = await fetch(`${USER_SERVICE_BASE_URL}/api/doctors/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      throw new Error('Invalid username or password');
    }
    const doctor = mapDoctor(await response.json());
    state.currentDoctor = doctor;
    state.currentPatient = null;
    writeStorage(CURRENT_DOCTOR_KEY, doctor);
    removeStorage(CURRENT_PATIENT_KEY);
    await this.loadQuestions({ doctorId: doctor.id });
    return doctor;
  },

  logoutDoctor() {
    state.currentDoctor = null;
    state.questions = [];
    removeStorage(CURRENT_DOCTOR_KEY);
  },

  async registerPatient(username: string, password: string, displayName: string): Promise<Patient> {
    const response = await fetch(`${USER_SERVICE_BASE_URL}/api/patients/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, displayName }),
    });
    if (!response.ok) {
      const err = await response.text();
      throw new Error(err || 'Register failed');
    }
    const data = await response.json();
    const patient: Patient = {
      id: String(data.id),
      username: data.username,
      displayName: data.displayName,
    };
    state.currentPatient = patient;
    state.currentDoctor = null;
    writeStorage(CURRENT_PATIENT_KEY, patient);
    removeStorage(CURRENT_DOCTOR_KEY);
    await this.loadQuestions({ patientId: patient.id });
    return patient;
  },

  async loginPatient(username: string, password: string): Promise<Patient> {
    const response = await fetch(`${USER_SERVICE_BASE_URL}/api/patients/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    if (!response.ok) {
      throw new Error('Invalid username or password');
    }
    const data = await response.json();
    const patient: Patient = {
      id: String(data.id),
      username: data.username,
      displayName: data.displayName,
    };
    state.currentPatient = patient;
    state.currentDoctor = null;
    writeStorage(CURRENT_PATIENT_KEY, patient);
    removeStorage(CURRENT_DOCTOR_KEY);
    await this.loadQuestions({ patientId: patient.id });
    return patient;
  },

  logoutPatient() {
    state.currentPatient = null;
    state.questions = [];
    removeStorage(CURRENT_PATIENT_KEY);
  },

  async loadQuestions(filters: { doctorId?: string; patientId?: string; status?: string } = {}): Promise<Question[]> {
    const params = new URLSearchParams();
    if (filters.doctorId) {
      params.set('doctorId', filters.doctorId);
    }
    if (filters.patientId) {
      params.set('patientId', filters.patientId);
    }
    if (filters.status) {
      params.set('status', filters.status);
    }
    const queryString = params.toString();
    const url = `${QUESTION_SERVICE_BASE_URL}/api/questions${queryString ? `?${queryString}` : ''}`;
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Failed to load questions');
    }
    const questions = (await response.json()).map(mapQuestion);
    state.questions = questions;
    return questions;
  },

  async addQuestion(question: Omit<Question, 'id' | 'submitTime' | 'status' | 'answer' | 'answerTime'>): Promise<Question> {
    const response = await fetch(`${QUESTION_SERVICE_BASE_URL}/api/questions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(question),
    });
    if (!response.ok) {
      throw new Error('Failed to submit question');
    }
    const createdQuestion = mapQuestion(await response.json());
    if (state.currentPatient) {
      await this.loadQuestions({ patientId: state.currentPatient.id });
    }
    return createdQuestion;
  },

  async answerQuestion(questionId: string, answer: string): Promise<Question> {
    const response = await fetch(`${QUESTION_SERVICE_BASE_URL}/api/questions/${questionId}/answer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ answer }),
    });
    if (!response.ok) {
      throw new Error('Failed to answer question');
    }
    const updatedQuestion = mapQuestion(await response.json());
    if (state.currentDoctor) {
      await this.loadQuestions({ doctorId: state.currentDoctor.id });
    }
    return updatedQuestion;
  },

  async markQuestionAsAnswered(questionId: string): Promise<Question> {
    const response = await fetch(`${QUESTION_SERVICE_BASE_URL}/api/questions/${questionId}/mark-answered`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error('Failed to update question status');
    }
    const updatedQuestion = mapQuestion(await response.json());
    if (state.currentDoctor) {
      await this.loadQuestions({ doctorId: state.currentDoctor.id });
    }
    return updatedQuestion;
  },

  getQuestionsByDoctor(doctorId: string): Question[] {
    return state.questions.filter((question) => question.doctorId === doctorId);
  },

  getQuestionsByPatient(patientId: string): Question[] {
    return state.questions.filter((question) => question.patientId === patientId);
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    return state.doctors.find((doctor) => doctor.username === username);
  },

  getActiveDoctors(): Doctor[] {
    return state.doctors.filter((doctor) => doctor.isActive);
  },

  getStatistics() {
    return {
      totalDoctors: state.doctors.length,
      totalQuestions: state.questions.length,
      activeSessions: state.questions.filter((question) => question.status === 'pending').length,
      totalSessions: state.doctors.filter((doctor) => doctor.isActive).length,
    };
  },
};
