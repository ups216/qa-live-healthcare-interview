import { reactive } from 'vue';
import { doctorApi, patientApi, questionApi, Doctor, Patient, Question } from '../api';

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
  patients: [],
  questions: [],
  currentDoctor: null,
  currentPatient: null,
  loading: false,
  error: null,
});

async function loadAllData() {
  state.loading = true;
  state.error = null;
  try {
    const [doctors, patients, questions] = await Promise.all([
      doctorApi.getAll(),
      patientApi.getAll(),
      questionApi.getAll(),
    ]);
    state.doctors = doctors;
    state.patients = patients;
    state.questions = questions;
  } catch (error) {
    state.error = error instanceof Error ? error.message : 'Failed to load data';
    console.error('Failed to load data:', error);
  } finally {
    state.loading = false;
  }
}

loadAllData();

export const store = {
  state,

  async reload() {
    await loadAllData();
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    const doctor = await doctorApi.getByUsername(username);
    if (doctor && doctor.password === password) {
      state.currentDoctor = doctor;
      return doctor;
    }
    return null;
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  async verifyPatient(name: string, birthday: string): Promise<Patient | null> {
    try {
      const result = await patientApi.verify(name, birthday);
      if (result.success && result.patient) {
        state.currentPatient = result.patient;
        return result.patient;
      } else {
        state.currentPatient = null;
        return null;
      }
    } catch (error) {
      console.error('验证患者失败:', error);
      state.currentPatient = null;
      return null;
    }
  },

  async loginPatient(name: string, password: string): Promise<{ success: boolean; message: string; patient?: Patient }> {
    try {
      const result = await patientApi.login(name, password);
      if (result.success && result.patient) {
        state.currentPatient = result.patient;
      }
      return result;
    } catch (error) {
      console.error('登录失败:', error);
      return { success: false, message: '登录失败，请稍后重试' };
    }
  },

  async resetPassword(name: string, birthday: string, newPassword: string): Promise<{ success: boolean; message: string }> {
    try {
      return await patientApi.resetPassword(name, birthday, newPassword);
    } catch (error) {
      console.error('密码重置失败:', error);
      return { success: false, message: '密码重置失败，请稍后重试' };
    }
  },

  async changePassword(id: string, oldPassword: string, newPassword: string): Promise<{ success: boolean; message: string }> {
    try {
      return await patientApi.changePassword(id, oldPassword, newPassword);
    } catch (error) {
      console.error('密码修改失败:', error);
      return { success: false, message: '密码修改失败，请稍后重试' };
    }
  },

  logoutPatient() {
    state.currentPatient = null;
  },

  async getQuestionsByDoctor(doctorId: string): Promise<Question[]> {
    return await questionApi.getByDoctorId(doctorId);
  },

  async getQuestionsByPatient(patientId: string): Promise<Question[]> {
    return await questionApi.getByPatientId(patientId);
  },

  async getAllQuestions(): Promise<Question[]> {
    return await questionApi.getAll();
  },

  async answerQuestion(questionId: string, answer: string): Promise<void> {
    await questionApi.answer(questionId, answer);
    await this.reload();
  },

  async markQuestionAsAnswered(questionId: string): Promise<void> {
    await questionApi.answer(questionId, '已口述解答');
    await this.reload();
  },

  async getDoctorByUsername(username: string): Promise<Doctor | undefined> {
    const doctor = await doctorApi.getByUsername(username);
    return doctor || undefined;
  },

  async getActiveDoctors(): Promise<Doctor[]> {
    return await doctorApi.getActive();
  },

  async getStatistics() {
    const [doctors, questions] = await Promise.all([
      doctorApi.getAll(),
      questionApi.getAll(),
    ]);

    const totalDoctors = doctors.length;
    const totalQuestions = questions.length;
    const activeSessions = questions.filter(q => q.status === 'pending').length;
    const totalSessions = doctors.filter(d => d.isActive).length;

    return {
      totalDoctors,
      totalQuestions,
      activeSessions,
      totalSessions,
    };
  },
};
