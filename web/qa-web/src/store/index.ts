import { reactive } from 'vue';
import questionData from '../data/question-list.json';
import { fetchDoctors } from '../api/doctors';
import {
  fetchCurrentPatient,
  getPatientToken,
  loginPatientApi,
  registerPatientApi,
  setPatientToken,
  type RegisterPatientPayload,
} from '../api/patients';
import type { Patient } from '../types/patient';

export type { Patient };

export interface Doctor {
  id: string;
  username: string;
  password: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
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
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

const state = reactive<State>({
  doctors: [],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

export const store = {
  state,

  async loadDoctors(force = false) {
    if (!force && state.doctors.length > 0) return;
    state.doctors = await fetchDoctors();
  },

  loginDoctor(username: string, password: string): Doctor | null {
    const doctor = state.doctors.find(
      d => d.username === username && d.password === password
    );
    if (doctor) {
      state.currentDoctor = doctor;
      return doctor;
    }
    return null;
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  async loginPatient(username: string, password: string): Promise<void> {
    const res = await loginPatientApi(username, password);
    setPatientToken(res.token);
    state.currentPatient = res.patient;
  },

  async registerPatient(payload: RegisterPatientPayload): Promise<void> {
    const res = await registerPatientApi(payload);
    setPatientToken(res.token);
    state.currentPatient = res.patient;
  },

  logoutPatient() {
    setPatientToken(null);
    state.currentPatient = null;
  },

  /**
   * Restores patient from JWT in localStorage (e.g. after full page refresh).
   */
  async initPatientSession(): Promise<void> {
    const token = getPatientToken();
    if (!token) {
      state.currentPatient = null;
      return;
    }
    try {
      state.currentPatient = await fetchCurrentPatient(token);
    } catch {
      setPatientToken(null);
      state.currentPatient = null;
    }
  },

  getQuestionsByDoctor(doctorId: string): Question[] {
    return state.questions.filter(q => q.doctorId === doctorId);
  },

  getQuestionsByPatient(patientId: string): Question[] {
    return state.questions.filter(q => q.patientId === patientId);
  },

  addQuestion(
    question: Omit<Question, 'id' | 'submitTime' | 'status' | 'answer' | 'answerTime'>
  ): Question {
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
    const q = state.questions.find(x => x.id === questionId);
    if (q) {
      q.status = 'answered';
      q.answer = answer;
      q.answerTime = new Date().toISOString();
    }
  },

  markQuestionAsAnswered(questionId: string) {
    const q = state.questions.find(x => x.id === questionId);
    if (q) {
      q.status = 'answered';
      q.answer = '已口述解答';
      q.answerTime = new Date().toISOString();
    }
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    return state.doctors.find(d => d.username === username);
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
