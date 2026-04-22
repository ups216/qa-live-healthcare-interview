<template>
  <div class="patient-login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" class="login-logo" />
          <h2>{{ $t('patientLogin.title') }}</h2>
          <p>{{ $t('patientLogin.subtitle') }}</p>
        </div>

        <a-form
          :model="loginForm"
          @finish="handleLogin"
          class="login-form"
        >
          <a-form-item
            name="username"
            :rules="[{ required: true, message: $t('patientLogin.usernameRequired') }]"
          >
            <a-input
              v-model:value="loginForm.username"
              size="large"
              :placeholder="$t('patientLogin.username')"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="password"
            :rules="[{ required: true, message: $t('patientLogin.passwordRequired') }]"
          >
            <a-input-password
              v-model:value="loginForm.password"
              size="large"
              :placeholder="$t('patientLogin.password')"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
            >
              {{ $t('patientLogin.loginBtn') }}
            </a-button>
          </a-form-item>

          <div class="login-footer">
            <a-button type="link" @click="goHome">
              {{ $t('patientLogin.backHome') }}
            </a-button>
          </div>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const router = useRouter();
const loading = ref(false);

const loginForm = reactive({
  username: '',
  password: '',
});

const handleLogin = async () => {
  loading.value = true;
  
  try {
    const success = await store.loginPatient(loginForm.username, loginForm.password);
    
    if (success) {
      message.success('登录成功');
      router.push('/');
    } else {
      message.error('用户名或密码错误');
    }
  } catch (error) {
    message.error('登录失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const goHome = () => {
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
  padding: 24px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
}

.login-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  margin-top: 24px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

@media (max-width: 480px) {
  .login-card {
    padding: 24px;
  }
}
</style>
