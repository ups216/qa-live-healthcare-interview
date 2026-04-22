import { reactive } from 'vue';
import questionData from '../data/question-list.json';

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
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

const API_BASE = '/api';

const state = reactive<State>({
  doctors: [] as Doctor[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

// 从 localStorage 恢复患者登录状态
const savedPatient = localStorage.getItem('qa_patient');
if (savedPatient) {
  try {
    state.currentPatient = JSON.parse(savedPatient);
  } catch {
    localStorage.removeItem('qa_patient');
  }
}

export const store = {
  state,

  // ========== 医生相关（后端 API） ==========

  async loadDoctors(): Promise<void> {
    try {
      const response = await fetch(`${API_BASE}/doctor/list`);
      const result = await response.json();
      if (result.code === 200 && result.data) {
        state.doctors = result.data;
      }
    } catch (error) {
      console.error('加载医生列表失败:', error);
    }
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      const response = await fetch(`${API_BASE}/doctor/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      const result = await response.json();

      if (result.code === 200 && result.data) {
        state.currentDoctor = result.data;
        return result.data;
      } else {
        return null;
      }
    } catch (error) {
      console.error('医生登录请求失败:', error);
      return null;
    }
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    return state.doctors.find(d => d.username === username);
  },

  getActiveDoctors(): Doctor[] {
    return state.doctors.filter(d => d.isActive);
  },

  // ========== 患者相关（后端 API） ==========

  async loginPatient(username: string, password: string): Promise<Patient | null> {
    try {
      const response = await fetch(`${API_BASE}/patient/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      const result = await response.json();

      if (result.code === 200 && result.data) {
        state.currentPatient = result.data;
        localStorage.setItem('qa_patient', JSON.stringify(result.data));
        return result.data;
      } else {
        return null;
      }
    } catch (error) {
      console.error('登录请求失败:', error);
      return null;
    }
  },

  async registerPatient(data: {
    username: string;
    password: string;
    name: string;
    birthday: string;
    phone?: string;
    gender?: string;
  }): Promise<{ success: boolean; message: string; patient?: Patient }> {
    try {
      const response = await fetch(`${API_BASE}/patient/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      const result = await response.json();

      if (result.code === 200 && result.data) {
        state.currentPatient = result.data;
        localStorage.setItem('qa_patient', JSON.stringify(result.data));
        return { success: true, message: result.message, patient: result.data };
      } else {
        return { success: false, message: result.message || '注册失败' };
      }
    } catch (error) {
      console.error('注册请求失败:', error);
      return { success: false, message: '网络异常，请稍后重试' };
    }
  },

  logoutPatient() {
    state.currentPatient = null;
    localStorage.removeItem('qa_patient');
  },

  // ========== 问诊问题相关 ==========

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

  // ========== 统计 ==========

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
