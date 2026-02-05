/**
 * 全局状态：医生/患者/问诊数据、首页语言、医生列表加载状态
 * 医生数据由 fetchDoctors() 从后端 API 拉取，供首页与医生页共用
 */
import { reactive } from 'vue';
import type { LocaleKey } from '../locales';
import patientData from '../data/patient-user.json';
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
  /** 医生列表，由 fetchDoctors 从 /api/doctors 拉取，不再使用本地 JSON */
  doctors: Doctor[];
  patients: Patient[];
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
  /** 首页语言：仅首页使用，与 locales 一致 */
  locale: LocaleKey;
  /** 医生列表是否正在请求中 */
  doctorsLoading: boolean;
  /** 医生列表请求失败时的错误信息，便于页面展示「加载失败」 */
  doctorsError: string | null;
}

const state = reactive<State>({
  doctors: [],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  locale: 'zh-CN',
  doctorsLoading: false,
  doctorsError: null,
});

export const store = {
  state,

  /** 设置首页语言，仅首页文案会响应 */
  setLocale(locale: LocaleKey) {
    state.locale = locale;
  },

  /**
   * 从后端拉取医生列表并写入 state.doctors
   * 供 App 挂载时或医生页调用；失败时设置 doctorsError，不抛错
   */
  async fetchDoctors(): Promise<void> {
    state.doctorsLoading = true;
    state.doctorsError = null;
    try {
      const res = await fetch('/api/doctors');
      if (!res.ok) {
        try {
          const text = await res.text();
          const j = text ? JSON.parse(text) : null;
          state.doctorsError = (j && typeof j.message === 'string') ? j.message : (res.statusText || '加载失败');
        } catch {
          state.doctorsError = res.statusText || '加载失败';
        }
        state.doctors = [];
        return;
      }
      let data: unknown;
      try {
        data = await res.json();
      } catch {
        state.doctorsError = '响应格式错误';
        state.doctors = [];
        return;
      }
      if (!Array.isArray(data)) {
        state.doctorsError = '数据格式异常';
        state.doctors = [];
        return;
      }
      state.doctors = data as Doctor[];
    } catch (e) {
      state.doctorsError = (e instanceof Error ? e.message : '网络错误') || '加载失败';
      state.doctors = [];
    } finally {
      state.doctorsLoading = false;
    }
  },

  /**
   * 医生登录：从当前 store.doctors 中匹配 username+password。
   * 注意：医生列表 API 不返回 password，故数据来自 API 时登录会失败，需后续对接登录 API。
   */
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

  /** 仅返回 isActive 为 true 的医生（null/undefined 视为离线） */
  getActiveDoctors(): Doctor[] {
    return state.doctors.filter(d => d.isActive === true);
  },

  getStatistics() {
    const totalDoctors = state.doctors.length;
    const totalQuestions = state.questions.length;
    const activeSessions = state.questions.filter(q => q.status === 'pending').length;
    const totalSessions = state.doctors.filter(d => d.isActive === true).length;

    return {
      totalDoctors,
      totalQuestions,
      activeSessions,
      totalSessions,
    };
  },
};
