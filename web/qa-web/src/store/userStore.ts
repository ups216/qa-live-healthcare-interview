import { reactive } from 'vue';
import { apiService } from '../services/api';

export interface User {
  id: number;
  username: string;
  name: string;
  email: string;
  phone: string;
  createdAt: string;
}

interface UserState {
  user: User | null;
  isLoggedIn: boolean;
  loading: boolean;
  error: string | null;
}

const state = reactive<UserState>({
  user: null,
  isLoggedIn: false,
  loading: false,
  error: null,
});

export const userStore = {
  state,
  
  async login(username: string, password: string): Promise<boolean> {
    state.loading = true;
    state.error = null;
    
    try {
      const response = await apiService.loginUser(username, password);
      
      if (response.success && response.user) {
        state.user = response.user;
        state.isLoggedIn = true;
        return true;
      } else {
        state.error = response.message || '登录失败';
        return false;
      }
    } catch (error) {
      state.error = '网络错误，请检查连接';
      console.error('Login error:', error);
      return false;
    } finally {
      state.loading = false;
    }
  },
  
  async register(username: string, password: string, name: string, email?: string, phone?: string): Promise<boolean> {
    state.loading = true;
    state.error = null;
    
    try {
      const response = await apiService.registerUser(username, password, name, email, phone);
      
      if (response.success && response.user) {
        state.user = response.user;
        state.isLoggedIn = true;
        return true;
      } else {
        state.error = response.message || '注册失败';
        return false;
      }
    } catch (error) {
      state.error = '网络错误，请检查连接';
      console.error('Register error:', error);
      return false;
    } finally {
      state.loading = false;
    }
  },
  
  logout(): void {
    state.user = null;
    state.isLoggedIn = false;
    state.error = null;
  },
  
  get errorMessage(): string | null {
    return state.error;
  }
};