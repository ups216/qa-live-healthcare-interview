import { reactive, computed } from 'vue';
import doctorDataZh from '../data/doctor-user-list.json';
import doctorDataEn from '../data/doctor-user-list-en.json';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
import i18n from '../locales';

// 获取当前语言的医生数据
const getDoctorDataByLocale = () => {
  const currentLocale = i18n.global.locale.value;
  return currentLocale === 'en' ? doctorDataEn : doctorDataZh;
};

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
  doctors: doctorDataZh as Doctor[],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

export const store = {
  state,

  // 获取当前语言的医生数据
  getDoctors(): Doctor[] {
    return getDoctorDataByLocale() as Doctor[];
  },

  loginDoctor(username: string, password: string): Doctor | null {
    const doctors = this.getDoctors();
    const doctor = doctors.find(
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
      question.answer = i18n.global.locale.value === 'en' ? 'Orally answered' : '已口述解答';
      question.answerTime = new Date().toISOString();
    }
  },

  getDoctorByUsername(username: string): Doctor | undefined {
    const doctors = this.getDoctors();
    return doctors.find(d => d.username === username);
  },

  getActiveDoctors(): Doctor[] {
    const doctors = this.getDoctors();
    return doctors.filter(d => d.isActive);
  },

  getStatistics() {
    const doctors = this.getDoctors();
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
