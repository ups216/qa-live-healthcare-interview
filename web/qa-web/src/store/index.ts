import { reactive } from 'vue';
// import patientData from '../data/patient-user.json'; // Data migrated to MySQL
import questionData from '../data/question-list.json';

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

export interface Patient {
  id: string;
  name: string;
  birthday: string;
  phone: string;
  gender: string;
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

interface State {
  doctors: Doctor[];
  patients: Patient[];
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

const state = reactive<State>({
  doctors: [] as Doctor[],
  patients: [] as Patient[], // Data is now fetched from API
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
}) as State;

async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(path);
  if (!res.ok) {
    throw new Error(`Request failed: ${res.status} ${res.statusText}`);
  }
  return (await res.json()) as T;
}

async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    throw new Error(`Request failed: ${res.status} ${res.statusText}`);
  }
  return (await res.json()) as T;
}

export const store = {
  state,

  async loadDoctors(): Promise<Doctor[]> {
    const doctors = await apiGet<Doctor[]>('/api/doctors');
    state.doctors = doctors as Doctor[];
    return doctors;
  },

  async refreshActiveDoctors(): Promise<Doctor[]> {
    const doctors = await apiGet<Doctor[]>('/api/doctors/active');
    // 仍然同步到 state，保持页面只读 state 的用法不变
    // 这里选择用 active 列表覆盖（也可以做 merge），以减少本次改造范围
    state.doctors = doctors as Doctor[];
    return doctors;
  },

  async getDoctorByUsername(username: string): Promise<Doctor | undefined> {
    try {
      return await apiGet<Doctor>(`/api/doctors/${encodeURIComponent(username)}`);
    } catch {
      return undefined;
    }
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      const doctor = await apiPost<Doctor>('/api/doctors/login', { username, password });
      state.currentDoctor = doctor;
      return doctor;
    } catch {
      return null;
    }
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  verifyPatient(name: string, birthday: string): Patient {
    let patient = state.patients.find(
      p => p.name === name && p.birthday === birthday
    );

    if (!patient) {
      patient = {
        id: `patient${Date.now()}`,
        name,
        birthday,
        phone: '',
        gender: '',
      };
      state.patients.push(patient);
    }

    state.currentPatient = patient;
    return patient;
  },

  logoutPatient() {
    state.currentPatient = null;
  },

  async verifyPatientByNameAndBirthday(name: string, birthday: string): Promise<Patient | null> {
    try {
      const patient = await apiPost<Patient>('/api/patients/verify', { name, birthday });
      state.currentPatient = patient;
      return patient;
    } catch {
      return null;
    }
  },

  async loginPatient(username: string, password: string): Promise<Patient | null> {
    try {
      const patient = await apiPost<Patient>('/api/patients/login', { username, password });
      state.currentPatient = patient;
      return patient;
    } catch {
      return null;
    }
  },

  async registerPatient(name: string, phone: string, gender?: string): Promise<any> {
    try {
      return await apiPost('/api/patients/register', { name, phone, gender });
    } catch (error) {
      throw error;
    }
  },

  getQuestionsByDoctor(doctorId: string): Question[] {
    return state.questions.filter(q => q.doctorId === doctorId);
  },

  getQuestionsByPatient(patientId: string): Question[] {
    return state.questions.filter(q => q.patientId === patientId);
  },

  addQuestion(question: Omit<Question, 'id' | 'submitTime' | 'status' | 'answer' | 'answerTime'>): Question {
    const newQuestion: Question = {
      ...question,
      id: `q${Date.now()}`,
      submitTime: new Date().toISOString(),
      status: 'pending',
      answer: null,
      answerTime: null,
    };
    state.questions.push(newQuestion);
    return newQuestion;
  },

  answerQuestion(questionId: string, answer: string) {
    const question = state.questions.find(q => q.id === questionId);
    if (question) {
      question.status = 'answered';
      question.answer = answer;
      question.answerTime = new Date().toISOString();
    }
  },

  markQuestionAsAnswered(questionId: string) {
    const question = state.questions.find(q => q.id === questionId);
    if (question) {
      question.status = 'answered';
      question.answer = '已口述解答';
      question.answerTime = new Date().toISOString();
    }
  },

  getActiveDoctors(): Doctor[] {
    return state.doctors.filter(d => d.isActive);
  },

  getStatistics() {
    const totalDoctors = state.doctors.length;
    const totalQuestions = state.questions.length;
    const activeSessions = state.questions.filter(q => q.status === 'pending').length;
    const totalSessions = state.doctors.filter(d => d.isActive).length;

    return {
      totalDoctors,
      totalQuestions,
      activeSessions,
      totalSessions,
    };
  },
};
