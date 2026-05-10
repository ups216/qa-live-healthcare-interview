<template>
  <div class="patient-login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>患者登录</h1>
        <p>欢迎使用医疗健康问答系统</p>
      </div>

      <LoginForm
        :loading="loading"
        :error="errorMessage"
        @submit="handleLogin"
      />

      <div class="login-footer">
        <p>测试账号: patient001 / Patient123</p>
        <a-button type="link" @click="goToHome">返回首页</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import LoginForm from '../components/LoginForm.vue';
import { useAuthStore } from '../store/auth';

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const errorMessage = ref('');

const handleLogin = async (credentials: { username: string; password: string }) => {
  loading.value = true;
  errorMessage.value = '';

  try {
    const result = await authStore.login(credentials);
    
    if (result.success) {
      // 登录成功，跳转到主页
      router.push('/');
    } else {
      errorMessage.value = result.message || '登录失败';
    }
  } catch (error) {
    errorMessage.value = '登录失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

const goToHome = () => {
  router.push('/');
};
</script>

<style scoped>
.patient-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 480px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #666;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}
</style>
