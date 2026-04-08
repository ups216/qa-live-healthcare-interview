import { reactive, computed, ref } from 'vue';
import { doctorApi } from '../api';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
import i18n from '../locales';

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
  loading: boolean;
  error: string | null;
}

const state = reactive<State>({
  doctors: [],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  loading: false,
  error: null,
});

// 缓存的医生数据
const doctorsCache = ref<Doctor[]>([]);

export const store = {
  state,

  // 从后端获取所有医生
  async fetchDoctors(): Promise<Doctor[]> {
    try {
      state.loading = true;
      state.error = null;
      const response = await doctorApi.getAllDoctors();
      if (response.data.code === 200) {
        doctorsCache.value = response.data.data;
        state.doctors = response.data.data;
        return response.data.data;
      }
      return [];
    } catch (error) {
      console.error('Failed to fetch doctors:', error);
      state.error = 'Failed to fetch doctors';
      return [];
    } finally {
      state.loading = false;
    }
  },

  // 从后端获取在线医生
  async fetchActiveDoctors(): Promise<Doctor[]> {
    try {
      state.loading = true;
      state.error = null;
      const response = await doctorApi.getActiveDoctors();
      if (response.data.code === 200) {
        return response.data.data;
      }
      return [];
    } catch (error) {
      console.error('Failed to fetch active doctors:', error);
      state.error = 'Failed to fetch active doctors';
      return [];
    } finally {
      state.loading = false;
    }
  },

  // 获取医生列表（优先使用缓存，如果没有则请求后端）
  async getDoctors(): Promise<Doctor[]> {
    if (doctorsCache.value.length === 0) {
      await this.fetchDoctors();
    }
    return doctorsCache.value;
  },

  // 获取在线医生列表
  async getActiveDoctors(): Promise<Doctor[]> {
    return await this.fetchActiveDoctors();
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      state.loading = true;
      state.error = null;
      const response = await doctorApi.login(username, password);
      if (response.data.code === 200) {
        state.currentDoctor = response.data.data;
        return response.data.data;
      }
      return null;
    } catch (error) {
      console.error('Login failed:', error);
      state.error = 'Login failed';
      return null;
    } finally {
      state.loading = false;
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
      question.answer = i18n.global.locale.value === 'en' ? 'Orally answered' : '已口述解答';
      question.answerTime = new Date().toISOString();
    }
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    return doctorsCache.value.find(d => d.username === username);
  },

  async getStatistics() {
    const doctors = await this.getDoctors();
    const totalDoctors = doctors.length;
    const totalQuestions = state.questions.length;
    const activeSessions = state.questions.filter(q => q.status === 'pending').length;
    const totalSessions = doctors.filter(d => d.isActive).length;

    return {
      totalDoctors,
      totalQuestions,
      activeSessions,
      totalSessions,
    };
  },
};
