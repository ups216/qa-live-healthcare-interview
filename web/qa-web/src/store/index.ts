import { reactive } from 'vue';
import axios from 'axios';
import doctorData from '../data/doctor-user-list.json';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';

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
  name: string;
  birthday: string;
  phone: string;
  gender: string;
}

export interface OutpatientUser {
  id: number;
  username: string;
  name: string;
  phone?: string;
  gender?: string;
  birthday?: string;
  status: number;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  userInfo: {
    id: number;
    username: string;
    name: string;
    status: number;
  };
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
  outpatientUser: OutpatientUser | null;
  token: string | null;
}

const TOKEN_KEY = 'outpatient_token';
const USER_KEY = 'outpatient_user';

// 从localStorage恢复登录状态
const savedToken = localStorage.getItem(TOKEN_KEY);
const savedUser = localStorage.getItem(USER_KEY);

const state = reactive<State>({
  doctors: doctorData as Doctor[],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  outpatientUser: savedUser ? JSON.parse(savedUser) : null,
  token: savedToken,
});

// 配置axios默认请求头
if (savedToken) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
}

export const store = {
  state,

  async fetchDoctors(): Promise<void> {
    try {
      const response = await axios.get('/api/doctor/list?page=1&pageSize=100');
      if (response.data.code === 200) {
        const pageData = response.data.data;
        state.doctors = pageData.list.map((doctor: any) => ({
          ...doctor,
          isActive: doctor.isActive,
          specialties: typeof doctor.specialties === 'string' 
            ? JSON.parse(doctor.specialties) 
            : doctor.specialties
        }));
      }
    } catch (error) {
      console.error('获取医生列表失败:', error);
    }
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

  // 门诊用户登录
  async loginOutpatient(username: string, password: string): Promise<{ success: boolean; message: string; remainingAttempts?: number }> {
    try {
      const response = await axios.post('/api/auth/login', {
        username,
        password,
      });

      if (response.data.code === 200) {
        const data: LoginResponse = response.data.data;
        // 保存Token和用户信息
        state.token = data.token;
        state.outpatientUser = {
          id: data.userInfo.id,
          username: data.userInfo.username,
          name: data.userInfo.name,
          status: data.userInfo.status,
        };
        
        // 持久化存储
        localStorage.setItem(TOKEN_KEY, data.token);
        localStorage.setItem(USER_KEY, JSON.stringify(state.outpatientUser));
        
        // 设置axios默认请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
        
        return { success: true, message: '登录成功' };
      } else {
        const data = response.data.data || {};
        return { 
          success: false, 
          message: response.data.message || '登录失败',
          remainingAttempts: data.remainingAttempts,
        };
      }
    } catch (error: any) {
      if (error.response?.data?.data?.remainingAttempts !== undefined) {
        return {
          success: false,
          message: error.response.data.message || '用户名或密码错误',
          remainingAttempts: error.response.data.data.remainingAttempts,
        };
      }
      return { success: false, message: error.response?.data?.message || '登录失败，请稍后重试' };
    }
  },

  // 门诊用户退出登录
  async logoutOutpatient(): Promise<void> {
    try {
      await axios.post('/api/auth/logout');
    } catch (error) {
      console.error('退出登录请求失败:', error);
    } finally {
      // 清除本地状态
      state.token = null;
      state.outpatientUser = null;
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      delete axios.defaults.headers.common['Authorization'];
    }
  },

  // 刷新Token
  async refreshToken(): Promise<boolean> {
    if (!state.token) return false;
    
    try {
      const response = await axios.post('/api/auth/refresh');
      if (response.data.code === 200) {
        const data: LoginResponse = response.data.data;
        state.token = data.token;
        localStorage.setItem(TOKEN_KEY, data.token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
        return true;
      }
      return false;
    } catch (error) {
      console.error('刷新Token失败:', error);
      return false;
    }
  },

  // 检查登录状态
  isLoggedIn(): boolean {
    return state.token !== null && state.outpatientUser !== null;
  },

  // 获取门诊用户信息
  getOutpatientUser(): OutpatientUser | null {
    return state.outpatientUser;
  },

  // 兼容旧方法：验证患者（保持与原有逻辑兼容）
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
};
