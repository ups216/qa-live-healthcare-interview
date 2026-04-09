<template>
  <div class="patient-login">
    <div class="login-container">
      <div class="login-header">
        <h1>患者登录</h1>
        <p>请登录后继续在线问诊</p>
      </div>

      <a-tabs v-model:activeKey="activeTab" centered>
        <a-tab-pane key="login" tab="登录">
          <a-form
            :model="loginForm"
            :rules="loginRules"
            @finish="onLogin"
            layout="vertical"
            class="login-form"
          >
            <a-form-item label="用户名" name="username">
              <a-input
                v-model:value="loginForm.username"
                size="large"
                placeholder="请输入用户名"
                autocomplete="username"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>
            <a-form-item label="密码" name="password">
              <a-input-password
                v-model:value="loginForm.password"
                size="large"
                placeholder="请输入密码"
                autocomplete="current-password"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" size="large" block :loading="loading">
                登录
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <a-tab-pane key="register" tab="注册">
          <a-form
            :model="registerForm"
            :rules="registerRules"
            @finish="onRegister"
            layout="vertical"
            class="login-form"
          >
            <a-form-item label="用户名" name="username">
              <a-input
                v-model:value="registerForm.username"
                size="large"
                placeholder="字母、数字、下划线，3～32 位"
                autocomplete="username"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>
            <a-form-item label="密码" name="password">
              <a-input-password
                v-model:value="registerForm.password"
                size="large"
                placeholder="至少 6 位"
                autocomplete="new-password"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>
            <a-form-item label="确认密码" name="confirm">
              <a-input-password
                v-model:value="registerForm.confirm"
                size="large"
                placeholder="再次输入密码"
                autocomplete="new-password"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>
            <a-form-item label="姓名（展示给医生）" name="name">
              <a-input v-model:value="registerForm.name" size="large" placeholder="真实姓名或称呼" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" size="large" block :loading="loading">
                注册并登录
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>

      <a-alert
        message="演示账号"
        description="用户名 zhaoming，密码 123456（与 PRD 种子数据一致；需后端与 MySQL 已启动并完成建表）"
        type="info"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const activeTab = ref('login');

const loginForm = reactive({
  username: '',
  password: '',
});

const registerForm = reactive({
  username: '',
  password: '',
  confirm: '',
  name: '',
});

const loginRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
};

const registerRules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 32, message: '用户名为 3～32 位' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字与下划线' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少 6 位' },
  ],
  confirm: [{ required: true, message: '请确认密码' }],
  name: [{ required: true, message: '请输入姓名' }],
};

const redirectAfterAuth = () => {
  const r = route.query.redirect as string | undefined;
  router.replace(r && r.startsWith('/') ? r : '/consultation');
};

const onLogin = async () => {
  loading.value = true;
  try {
    await store.loginPatient(loginForm.username, loginForm.password);
    message.success('登录成功');
    redirectAfterAuth();
  } catch (e) {
    message.error(e instanceof Error ? e.message : '登录失败');
  } finally {
    loading.value = false;
  }
};

const onRegister = async () => {
  if (registerForm.password !== registerForm.confirm) {
    message.error('两次输入的密码不一致');
    return;
  }
  loading.value = true;
  try {
    await store.registerPatient({
      username: registerForm.username,
      password: registerForm.password,
      name: registerForm.name,
    });
    message.success('注册成功');
    redirectAfterAuth();
  } catch (e) {
    message.error(e instanceof Error ? e.message : '注册失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.patient-login {
  min-height: 100vh;
  padding-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 88px 16px 32px;
}

.login-container {
  width: 100%;
  max-width: 440px;
  background: #fff;
  border-radius: 16px;
  padding: 32px 40px 28px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 8px;
}

.login-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px;
}

.login-header p {
  color: #888;
  margin: 0 0 16px;
}

.login-form {
  margin-top: 8px;
}

.login-container :deep(.ant-tabs-nav) {
  margin-bottom: 16px;
}
</style>
