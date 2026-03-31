import axios from 'axios';
import i18n from '../locales';

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 自动添加当前语言参数
api.interceptors.request.use((config) => {
  const currentLocale = i18n.global.locale.value;
  if (config.params) {
    config.params.lang = currentLocale;
  } else {
    config.params = { lang: currentLocale };
  }
  return config;
});

// 医生相关 API
export const doctorApi = {
  // 获取所有医生
  getAllDoctors() {
    return api.get('/doctors');
  },
  
  // 获取在线医生
  getActiveDoctors() {
    return api.get('/doctors/active');
  },
  
  // 获取医生详情
  getDoctorById(id: string) {
    return api.get(`/doctors/${id}`);
  },
  
  // 根据用户名获取医生
  getDoctorByUsername(username: string) {
    return api.get(`/doctors/username/${username}`);
  },
  
  // 医生登录
  login(username: string, password: string) {
    return api.post('/doctors/login', { username, password });
  },
};

export default api;
