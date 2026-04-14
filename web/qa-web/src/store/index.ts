import { reactive } from 'vue';
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
  isLoading: boolean;
}

const state = reactive<State>({
  doctors: [],
  patients: patientData as Patient[],
  questions: questionData as Question[],
  currentDoctor: null,
  currentPatient: null,
  isLoading: false,
});

export const store = {
  state,
  
  // 初始化加载医生数据
  async init() {
    if (state.doctors.length === 0 && !state.isLoading) {
      state.isLoading = true;
      try {
        const response = await fetch('http://localhost:8080/api/doctors/active');
        const result = await response.json();
        
        if (result.success && result.data) {
          // 转换API返回的数据格式为前端需要的格式
          state.doctors = result.data.map((doctor: any) => ({
            id: doctor.id,
            username: doctor.username,
            password: '', // API不返回密码，使用空字符串
            name: doctor.name,
            title: doctor.title,
            department: doctor.department,
            avatar: doctor.avatar,
            experience: doctor.experience,
            specialties: doctor.specialties || [],
            isActive: doctor.isActive !== false
          }));
          console.log('✅ 从API加载医生数据成功:', state.doctors.length, '位医生');
        } else {
          // API返回但数据格式不正确，使用备用数据
          await this.loadFallbackData();
        }
      } catch (error) {
        console.warn('⚠️ 从API加载医生数据失败，使用备用数据:', error.message);
        await this.loadFallbackData();
      } finally {
        state.isLoading = false;
      }
    }
  },
  
  // 加载备用数据
  async loadFallbackData() {
    try {
      // 尝试从本地JSON文件加载
      const response = await fetch('/src/data/doctor-user-list.json');
      const doctorData = await response.json();
      state.doctors = doctorData as Doctor[];
      console.log('✅ 从本地JSON文件加载医生数据成功:', state.doctors.length, '位医生');
    } catch (error) {
      console.error('❌ 从本地文件加载数据失败:', error.message);
      // 使用硬编码的备用数据
      state.doctors = [
        {
          id: 'doc001',
          username: 'dr-zhang-wei',
          password: '123456',
          name: '张伟医生',
          title: '主任医师',
          department: '心内科',
          avatar: 'https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400',
          experience: '15年临床经验',
          specialties: ['高血压', '冠心病', '心律失常'],
          isActive: true
        },
        {
          id: 'doc002',
          username: 'dr-li-na',
          password: '123456',
          name: '李娜医生',
          title: '副主任医师',
          department: '儿科',
          avatar: 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400',
          experience: '10年临床经验',
          specialties: ['儿童感冒', '儿童发育', '疫苗接种'],
          isActive: true
        }
      ];
      console.log('✅ 使用硬编码备用数据');
    }
  },

  async loginDoctor(username: string, password: string): Promise<Doctor | null> {
    try {
      // 首先确保数据已加载
      if (state.doctors.length === 0) {
        await this.init();
      }
      
      // 模拟API登录 - 实际项目中这里应该调用后端登录接口
      const doctor = state.doctors.find(
        d => d.username === username && d.password === password
      );
      if (doctor) {
        state.currentDoctor = doctor;
        return doctor;
      }
      return null;
    } catch (error) {
      console.error('Login error:', error);
      return null;
    }
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

  async getDoctorByUsername(username: string): Promise<Doctor | undefined> {
    try {
      // 先检查内存中是否有数据
      const cachedDoctor = state.doctors.find(d => d.username === username);
      if (cachedDoctor) {
        return cachedDoctor;
      }
      
      // 如果内存中没有，调用API获取
      const response = await fetch(`http://localhost:8080/api/doctors/username/${username}`);
      const result = await response.json();
      
      if (result.success && result.data) {
        const doctorData = result.data;
        const doctor: Doctor = {
          id: doctorData.id,
          username: doctorData.username,
          password: '', // API不返回密码
          name: doctorData.name,
          title: doctorData.title,
          department: doctorData.department,
          avatar: doctorData.avatar,
          experience: doctorData.experience,
          specialties: doctorData.specialties || [],
          isActive: doctorData.isActive !== false
        };
        
        // 缓存到内存中
        state.doctors.push(doctor);
        return doctor;
      }
    } catch (error) {
      console.error(`Failed to get doctor by username ${username}:`, error);
    }
    return undefined;
  },

  // 患者登录
  async loginPatient(username: string, password: string): Promise<{success: boolean; message?: string; data?: any}> {
    try {
      const response = await fetch('http://localhost:8080/api/patients/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      const result = await response.json();
      
      if (result.success && result.data) {
        const patientData = result.data;
        // 检查是否返回了patient信息
        if (patientData.patient) {
          // 更新store中的当前患者
          state.currentPatient = {
            id: patientData.patient.id,
            name: patientData.patient.name,
            birthday: patientData.patient.birthday || '',
            phone: patientData.patient.phone || '',
            gender: patientData.patient.gender || '',
          };
        }
        return { success: true, message: result.message, data: patientData };
      } else {
        return { success: false, message: result.message || '登录失败' };
      }
    } catch (error) {
      console.error('患者登录API调用失败:', error);
      return { 
        success: false, 
        message: '网络连接失败，请检查后端服务是否运行'
      };
    }
  },

  getActiveDoctors(): Doctor[] {
    return state.doctors.filter(d => d.isActive);
  },

  async getStatistics() {
    try {
      const response = await fetch('http://localhost:8080/api/doctors/statistics');
      const result = await response.json();
      
      if (result.success && result.data) {
        const stats = result.data;
        // 补充前端需要的额外统计信息
        return {
          totalDoctors: stats.totalDoctors || 0,
          totalQuestions: state.questions.length,
          activeSessions: state.questions.filter(q => q.status === 'pending').length,
          totalSessions: stats.activeDoctors || 0,
        };
      }
      
      // 如果API调用失败，回退到本地计算
      return {
        totalDoctors: state.doctors.length,
        totalQuestions: state.questions.length,
        activeSessions: state.questions.filter(q => q.status === 'pending').length,
        totalSessions: state.doctors.filter(d => d.isActive).length,
      };
    } catch (error) {
      console.error('Failed to load statistics from API:', error);
      // 回退到本地计算
      return {
        totalDoctors: state.doctors.length,
        totalQuestions: state.questions.length,
        activeSessions: state.questions.filter(q => q.status === 'pending').length,
        totalSessions: state.doctors.filter(d => d.isActive).length,
      };
    }
  },
};
