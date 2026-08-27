import { reactive } from 'vue';
import questionData from '../data/question-list.json';

const API_BASE_URL = 'http://localhost:8080/api';

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

export interface Patient {
  id: string;
  username: string;
  name: string;
  phone?: string;
  gender?: string;
  birthday?: string;
}

export interface RegisterData {
  username: string;
  password: string;
  name: string;
  phone?: string;
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
  doctors: [],
  patients: [],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

export const store = {
  state,

  async fetchDoctors() {
    try {
      const response = await fetch(`${API_BASE_URL}/doctors`);
      if (response.ok) {
        const data = await response.json();
        state.doctors = data.map((d: any) => ({
          id: d.id,
          username: d.username,
          password: d.password,
          name: d.name,
          title: d.title,
          department: d.department,
          avatar: d.avatar,
          experience: d.experience,
          specialties: d.specialties || [],
          isActive: d.isActive ?? d.is_active
        }));
      }
    } catch (error) {
      console.error('Failed to fetch doctors:', error);
    }
  },

  async fetchActiveDoctors() {
    try {
      const response = await fetch(`${API_BASE_URL}/doctors/active`);
      if (response.ok) {
        const data = await response.json();
        state.doctors = data.map((d: any) => ({
          id: d.id,
          username: d.username,
          password: d.password,
          name: d.name,
          title: d.title,
          department: d.department,
          avatar: d.avatar,
          experience: d.experience,
          specialties: d.specialties || [],
          isActive: d.isActive ?? d.is_active
        }));
      }
    } catch (error) {
      console.error('Failed to fetch active doctors:', error);
    }
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      const response = await fetch(`${API_BASE_URL}/doctors/${username}`);
      if (response.ok) {
        const doctor = await response.json();
        if (doctor && doctor.password === password) {
          const mappedDoctor = {
            ...doctor,
            isActive: doctor.isActive ?? doctor.is_active
          };
          state.currentDoctor = mappedDoctor;
          return mappedDoctor;
        }
      }
    } catch (error) {
      console.error('Login failed:', error);
    }
    return null;
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  async loginPatient(username: string, password: string): Promise<Patient | null> {
    try {
      const response = await fetch(`${API_BASE_URL}/patients/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const patient = await response.json();
        state.currentPatient = patient;
        localStorage.setItem('currentPatient', JSON.stringify(patient));
        return patient;
      }
    } catch (error) {
      console.error('Patient login failed:', error);
    }
    return null;
  },

  async registerPatient(data: RegisterData): Promise<Patient | null> {
    try {
      const response = await fetch(`${API_BASE_URL}/patients/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const patient = await response.json();
        state.currentPatient = patient;
        localStorage.setItem('currentPatient', JSON.stringify(patient));
        return patient;
      } else {
        const error = await response.json();
        throw new Error(error.message || '注册失败');
      }
    } catch (error) {
      console.error('Patient register failed:', error);
      throw error;
    }
  },

  restorePatient() {
    const stored = localStorage.getItem('currentPatient');
    if (stored) {
      try {
        state.currentPatient = JSON.parse(stored);
      } catch (e) {
        localStorage.removeItem('currentPatient');
      }
    }
  },

  logoutPatient() {
    state.currentPatient = null;
    localStorage.removeItem('currentPatient');
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
