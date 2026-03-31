import { reactive } from 'vue';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
import { getAllDoctors, getActiveDoctors, getDoctorByUsername, type Doctor } from '../api/doctor';

export { type Doctor };

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
  doctorsLoaded: boolean;
  currentLocale: string;
}

const state = reactive<State>({
  doctors: [],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  doctorsLoaded: false,
  currentLocale: 'zh-CN',
});

async function loadDoctors(locale: string = 'zh-CN') {
  if (state.doctorsLoaded && state.currentLocale === locale) return;
  try {
    const doctors = await getAllDoctors(locale);
    state.doctors = doctors;
    state.doctorsLoaded = true;
    state.currentLocale = locale;
  } catch (error) {
    console.error('Failed to load doctors from API:', error);
  }
}

export const store = {
  state,

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    await loadDoctors();
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

  async getDoctorByUsername(username: string, locale: string = 'zh-CN'): Promise<Doctor | undefined> {
    await loadDoctors(locale);
    return state.doctors.find(d => d.username === username);
  },

  async getActiveDoctors(locale: string = 'zh-CN'): Promise<Doctor[]> {
    await loadDoctors(locale);
    return state.doctors.filter(d => d.isActive);
  },

  async getStatistics() {
    await loadDoctors();
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
