import { reactive } from 'vue';
import doctorData from '../data/doctor-user-list.json';
import patientData from '../data/patient-user.json';
import questionData from '../data/question-list.json';
import { fetchDoctors, type DoctorVO } from '../api/doctor';
import {
  fetchMe,
  login as apiLogin,
  logout as apiLogout,
  register as apiRegister,
  setToken,
  getToken,
} from '../api/patient';
import type { PatientVO, RegisterPayload } from '../api/patient';

/**
 * 医生对外模型。
 *
 * ⚠️ 已移除 password 字段：后端 DoctorVO 不再下发密码（安全要求）。
 * 医生登录需要的本地凭证见下方 DOCTOR_LOCAL_CREDENTIALS 说明。
 */
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

/**
 * 医生登录用的本地凭证表（username → password）。
 *
 * 背景：需求2 要求医生数据入库，但为安全起见后端 VO 不下发 password；
 * 而需求3 只要求为"问诊用户"做账号登录，医生登录（DoctorLogin.vue）不在改造范围。
 * 为了使 DoctorLogin.vue 保持零改动，这里基于既有 JSON 维护一份仅供比对用的凭证。
 *
 * 这是本方案的显式取舍（见 IMPLEMENTATION_PLAN.md 决策 D5）：
 * 后续若要彻底服务端化医生鉴权，应新增 /api/doctor/login 并删除本常量。
 */
const DOCTOR_LOCAL_CREDENTIALS: Record<string, string> = Object.fromEntries(
  doctorData.map((d) => [d.username, d.password])
);

/**
 * 问诊用户（患者）模型。
 *
 * 需求3 之后患者由数据库持久化并通过 JWT 鉴权，
 * 相比旧模型新增 username；birthday/phone/gender 为选填，故允许为 null。
 */
export interface Patient {
  id: string;
  username: string;
  name: string;
  birthday: string | null;
  phone: string | null;
  gender: string | null;
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
  /** 医生数据是否正在加载：供 Doctors.vue / Home.vue 判断 loading 态 */
  doctorsLoading: boolean;
  /** 医生数据加载失败原因：为 null 表示未出错 */
  doctorsError: string | null;
}

const state = reactive<State>({
  // 需求2：医生数据改由后端 MySQL 提供，初值为空数组，由 loadDoctors() 填充。
  // 首页与医生页在加载完成前会短暂显示 0 / 空列表，故需要用 loading 态加以区分。
  doctors: [],
  // 历史 mock 数据中不含 username（需求3 之前患者以"姓名+生日"识别），
  // 这里显式补一个空串以对齐新模型，避免类型断裂。
  patients: patientData.map((p) => ({ ...p, username: '' })) as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  doctorsLoading: false,
  doctorsError: null,
});

/** 后端 DoctorVO → 前端 Doctor（字段结构一致，仅做显式映射以便未来演进） */
function toDoctor(vo: DoctorVO): Doctor {
  return {
    id: vo.id,
    username: vo.username,
    name: vo.name,
    title: vo.title,
    department: vo.department,
    avatar: vo.avatar,
    experience: vo.experience,
    specialties: vo.specialties ?? [],
    isActive: Boolean(vo.isActive),
  };
}

/** 后端 PatientVO → 前端 Patient */
function toPatient(vo: PatientVO): Patient {
  return {
    id: vo.id,
    username: vo.username,
    name: vo.name,
    birthday: vo.birthday,
    phone: vo.phone,
    gender: vo.gender,
  };
}

export const store = {
  state,

  /**
   * 从后端加载医生数据并写入 state.doctors。
   *
   * 之所以在 store 层统一加载而非只在 Doctors.vue 各自拉取：
   * Home.vue 的统计、Consultation.vue 的医生下拉与 selectedDoctor 都消费同一个 doctors 数组，
   * 统一加载可以让这些视图全部零改动，避免连锁回归。
   * 异常向上抛出，由调用方（retry 场景）决定如何处理。
   */
  async loadDoctors(): Promise<void> {
    state.doctorsLoading = true;
    state.doctorsError = null;
    try {
      const list = await fetchDoctors();
      state.doctors = list.map(toDoctor);
    } catch (e) {
      state.doctors = [];
      state.doctorsError = e instanceof Error ? e.message : '加载医生列表失败';
      throw e;
    } finally {
      state.doctorsLoading = false;
    }
  },

  loginDoctor(username: string, password: string): Doctor | null {
    // 后端不下发密码，改与本地凭证表比对（详见 DOCTOR_LOCAL_CREDENTIALS 注释）
    if (DOCTOR_LOCAL_CREDENTIALS[username] !== password) {
      return null;
    }
    const doctor = state.doctors.find(d => d.username === username);
    if (doctor) {
      state.currentDoctor = doctor;
      return doctor;
    }
    return null;
  },

  logoutDoctor() {
    state.currentDoctor = null;
  },

  /**
   * 注册问诊用户。
   *
   * 注意这里**不再**像旧的 verifyPatient 那样"查不到就自动建档"：
   * 用户名重复时后端返回 400/1001，错误会向上抛出，由表单展示给用户。
   */
  async registerPatient(payload: RegisterPayload): Promise<void> {
    await apiRegister(payload);
  },

  /**
   * 用户名密码登录（需求3）。
   * 成功时写入 token 并拉取用户信息；失败时向上抛出后端 message。
   */
  async loginPatient(username: string, password: string): Promise<Patient> {
    const result = await apiLogin(username, password);
    setToken(result.token);
    state.currentPatient = toPatient(result.patient);
    return state.currentPatient;
  },

  /**
   * 依据本地 token 恢复登录态。
   *
   * 用于页面刷新后重建会话（旧实现把登录态放在内存，刷新即丢失）。
   * token 无效时会清理本地凭证并返回 false，调用方据此判定为未登录。
   */
  async restorePatient(): Promise<boolean> {
    if (!getToken()) {
      return false;
    }
    const vo = await fetchMe();
    if (!vo) {
      state.currentPatient = null;
      return false;
    }
    state.currentPatient = toPatient(vo);
    return true;
  },

  logoutPatient() {
    // 服务端 JWT 无状态，无需调用注销接口，清理本地凭证即可
    apiLogout();
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
