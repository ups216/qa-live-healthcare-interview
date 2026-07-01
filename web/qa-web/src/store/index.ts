import { reactive } from 'vue';
import questionData from '../data/question-list.json';
import * as doctorApi from '../api/doctor';
import * as patientApi from '../api/patient';

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
  doctorsLoaded: boolean;
  doctorsLoading: boolean;
  patients: Patient[];
  questions: Question[];
  currentDoctor: Doctor | null;
  currentPatient: Patient | null;
}

const state = reactive<State>({
  doctors: [],
  doctorsLoaded: false,
  doctorsLoading: false,
  patients: [],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
});

export const store = {
  state,

  async fetchDoctors(): Promise<void> {
    if (state.doctorsLoading) {
      return;
    }
    state.doctorsLoading = true;
    try {
      state.doctors = await doctorApi.fetchDoctors();
      state.doctorsLoaded = true;
    } finally {
      state.doctorsLoading = false;
    }
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      const doctor = await doctorApi.loginDoctor(username, password);
      state.currentDoctor = doctor;
      const index = state.doctors.findIndex(d => d.id === doctor.id);
      if (index >= 0) {
        state.doctors[index] = doctor;
      } else {
        state.doctors.push(doctor);
      }
      return doctor;
    } catch {
      return null;
    }
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  async loginPatient(username: string, password: string): Promise<Patient | null> {
    try {
      const patient = await patientApi.loginPatient(username, password);
      state.currentPatient = patient;
      const index = state.patients.findIndex(p => p.id === patient.id);
      if (index >= 0) {
        state.patients[index] = patient;
      } else {
        state.patients.push(patient);
      }
      return patient;
    } catch {
      return null;
    }
  },

  async registerPatient(payload: patientApi.PatientRegisterPayload): Promise<Patient | null> {
    try {
      const patient = await patientApi.registerPatient(payload);
      state.currentPatient = patient;
      state.patients.push(patient);
      return patient;
    } catch {
      return null;
    }
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
