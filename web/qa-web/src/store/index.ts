/**
 * 全局状态：医生列表来自后端 API（GET /api/doctors），患者与问题仍使用本地 JSON。
 * 医生登录校验使用本地密码映射（不暴露于 API），与 API 数据源一致。
 */
import { reactive } from 'vue';
import doctorData from '../data/doctor-user-list.json';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';

/** 医生（API 返回不含 password；登录校验用本地映射） */
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
  /** 仅登录用，API 不返回，本地校验时使用 */
  password?: string;
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
  doctorsLoading: boolean;
  doctorsError: string | null;
  patients: Patient[];
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

/** 仅用于医生登录校验的 username -> password 映射（来自原 JSON，不通过 API 暴露） */
const doctorPasswords: Record<string, string> = (doctorData as { username: string; password: string }[]).reduce(
  (acc, d) => {
    acc[d.username] = d.password;
    return acc;
  },
  {} as Record<string, string>
);

const state = reactive<State>({
  doctors: [],
  doctorsLoading: false,
  doctorsError: null,
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

const API_BASE = '/api';

/** 进行中的医生列表请求 Promise，并发调用时复用同一请求 */
let doctorsFetchPromise: Promise<void> | null = null;

export const store = {
  state,

  /**
   * 从后端获取医生列表，用于医生页与首页统计/开放诊室。
   * 并发调用时返回同一 Promise，只发一次请求，所有调用方均可 await 同一结果。
   */
  async fetchDoctors(): Promise<void> {
    if (state.doctorsLoading && doctorsFetchPromise) {
      return doctorsFetchPromise;
    }
    state.doctorsLoading = true;
    state.doctorsError = null;
    doctorsFetchPromise = (async () => {
      try {
        const res = await fetch(`${API_BASE}/doctors`, { method: 'GET' });
        if (!res.ok) {
          throw new Error(res.status === 500 ? '服务暂时不可用' : `请求失败: ${res.status}`);
        }
        const data = await res.json();
        if (!Array.isArray(data)) {
          throw new Error('响应格式错误');
        }
        state.doctors = data as Doctor[];
      } catch (e) {
        state.doctorsError = e instanceof Error ? e.message : '加载失败';
        state.doctors = [];
      } finally {
        state.doctorsLoading = false;
        doctorsFetchPromise = null;
      }
    })();
    return doctorsFetchPromise;
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    const pwd = doctorPasswords[username];
    if (pwd !== password) return null;
    if (!state.doctors.length && !state.doctorsLoading) {
      await this.fetchDoctors();
    }
    const doctor = state.doctors.find((d) => d.username === username);
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
    let patient = state.patients.find((p) => p.name === name && p.birthday === birthday);

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
    return state.questions.filter((q) => q.doctorId === doctorId);
  },

  getQuestionsByPatient(patientId: string): Question[] {
    return state.questions.filter((q) => q.patientId === patientId);
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
    const question = state.questions.find((q) => q.id === questionId);
    if (question) {
      question.status = 'answered';
      question.answer = answer;
      question.answerTime = new Date().toISOString();
    }
  },

  markQuestionAsAnswered(questionId: string) {
    const question = state.questions.find((q) => q.id === questionId);
    if (question) {
      question.status = 'answered';
      question.answer = '已口述解答';
      question.answerTime = new Date().toISOString();
    }
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    return state.doctors.find((d) => d.username === username);
  },

  getActiveDoctors(): Doctor[] {
    return state.doctors.filter((d) => d.isActive);
  },

  getStatistics() {
    const totalDoctors = state.doctors.length;
    const totalQuestions = state.questions.length;
    const activeSessions = state.questions.filter((q) => q.status === 'pending').length;
    const totalSessions = state.doctors.filter((d) => d.isActive).length;

    return {
      totalDoctors,
      totalQuestions,
      activeSessions,
      totalSessions,
    };
  },
};
