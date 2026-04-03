import { reactive, ref } from 'vue';
import { apiService, type Doctor as ApiDoctor, type ApiResponse } from '../services/api';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';

export interface Doctor extends ApiDoctor {}

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

// 响应式引用用于存储异步加载的医生数据
const doctorsRef = ref<Doctor[]>([]);

export const store = {
  state,
  doctors: doctorsRef,

  // 加载医生数据
  async loadDoctors() {
    state.loading = true;
    state.error = null;
    
    try {
      const response = await apiService.getAllDoctors();
      if (response.success && response.data) {
        state.doctors = response.data;
        doctorsRef.value = response.data;
        console.log('成功加载医生数据:', response.data.length, '位医生');
      } else {
        state.error = response.message || '加载医生数据失败';
        console.error('Failed to load doctors:', response.message);
      }
    } catch (error) {
      state.error = error instanceof Error ? error.message : '网络请求失败';
      console.error('Error loading doctors:', error);
    } finally {
      state.loading = false;
    }
  },

  // 医生登录
  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    state.loading = true;
    state.error = null;
    
    try {
      const response = await apiService.login({ username, password });
      if (response.success && response.data) {
        state.currentDoctor = response.data;
        return response.data;
      } else {
        state.error = response.message || '登录失败';
        return null;
      }
    } catch (error) {
      state.error = error instanceof Error ? error.message : '网络请求失败';
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

  // 获取加载状态
  get isLoading(): boolean {
    return state.loading;
  },

  // 获取错误信息
  get errorMessage(): string | null {
    return state.error;
  },

  // 清除错误信息
  clearError() {
    state.error = null;
  },
};