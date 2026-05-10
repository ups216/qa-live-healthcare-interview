import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { patientApi, LoginRequest, UserDTO } from '../api/patient';

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null);
  const user = ref<UserDTO | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isAuthenticated = computed(() => !!token.value);

  // 从localStorage恢复登录状态
  function initAuth() {
    const savedToken = localStorage.getItem('patient_token');
    const savedUser = localStorage.getItem('patient_user');
    
    if (savedToken && savedUser) {
      token.value = savedToken;
      user.value = JSON.parse(savedUser);
    }
  }

  // 登录
  async function login(credentials: LoginRequest) {
    loading.value = true;
    error.value = null;
    
    try {
      const response = await patientApi.login(credentials);
      
      if (response.code === 200 && response.data) {
        token.value = response.data.token;
        user.value = response.data.user;
        
        // 保存到localStorage
        localStorage.setItem('patient_token', response.data.token);
        localStorage.setItem('patient_user', JSON.stringify(response.data.user));
        
        return { success: true };
      } else {
        error.value = response.message || '登录失败';
        return { success: false, message: response.message };
      }
    } catch (err: any) {
      if (err.response) {
        const status = err.response.status;
        const data = err.response.data;
        
        if (status === 401) {
          error.value = '用户名或密码错误';
        } else if (status === 423) {
          error.value = '账户已被锁定，请稍后重试';
        } else if (status === 403) {
          error.value = '账户未激活';
        } else {
          error.value = data?.message || '登录失败，请稍后重试';
        }
      } else {
        error.value = '网络错误，请检查网络连接';
      }
      
      return { success: false, message: error.value };
    } finally {
      loading.value = false;
    }
  }

  // 登出
  function logout() {
    token.value = null;
    user.value = null;
    error.value = null;
    
    // 清除localStorage
    localStorage.removeItem('patient_token');
    localStorage.removeItem('patient_user');
  }

  // 获取当前用户信息
  async function fetchCurrentUser() {
    if (!token.value) return;
    
    try {
      const response = await patientApi.getCurrentUser(token.value);
      
      if (response.code === 200 && response.data) {
        user.value = response.data;
        localStorage.setItem('patient_user', JSON.stringify(response.data));
      }
    } catch (err) {
      console.error('获取用户信息失败:', err);
      logout();
    }
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    login,
    logout,
    fetchCurrentUser,
    initAuth,
  };
});
