import { reactive, computed } from 'vue';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
import { getAllDoctors, ApiDoctor } from '../api/doctor';

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

/** 患者用户（来自本地 JSON） */
export interface Patient {
  id: string;
  name: string;
  birthday: string;
  phone: string;
  gender: string;
}

/** 登录后的患者用户信息（来自后端 API） */
export interface PatientUser {
  id: string;
  username: string;
  name: string;
  gender: string;
  birthday: string;
  phone?: string;
  active: boolean;
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
  currentPatientUser: PatientUser | null;  // API 登录后的患者用户
}

const state = reactive<State>({
  doctors: [],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  currentPatientUser: restorePatientUser(),
});

/**
 * 从 localStorage 恢复患者登录态
 */
function restorePatientUser(): PatientUser | null {
  try {
    const saved = localStorage.getItem('patientUser');
    return saved ? JSON.parse(saved) : null;
  } catch {
    return null;
  }
}

function persistPatientUser(user: PatientUser | null) {
  if (user) {
    localStorage.setItem('patientUser', JSON.stringify(user));
  } else {
    localStorage.removeItem('patientUser');
  }
}

export const store = {
  state,

  /**
   * 从后端API加载医生数据
   */
  async fetchDoctors(): Promise<void> {
    const apiDoctors = await getAllDoctors();
    state.doctors = apiDoctors.map(apiDoc => ({
      id: apiDoc.id,
      username: apiDoc.username,
      password: '',
      name: apiDoc.name,
      title: apiDoc.title,
      department: apiDoc.department,
      avatar: apiDoc.avatar,
      experience: apiDoc.experience,
      specialties: apiDoc.specialties,
      isActive: apiDoc.active,
    })) as Doctor[];
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
    state.currentPatientUser = null;
    persistPatientUser(null);
  },

  /**
   * 通过 API 登录后设置当前登录的患者用户（持久化到 localStorage）
   */
  loginPatient(user: PatientUser) {
    state.currentPatientUser = user;
    persistPatientUser(user);
  },

  /**
   * 判断患者是否已通过 API 登录
   */
  isPatientLoggedIn(): boolean {
    return state.currentPatientUser !== null;
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
