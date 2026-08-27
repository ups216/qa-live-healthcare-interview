<template>
  <div class="patient-register">
    <div class="register-container">
      <div class="register-header">
        <h1>患者注册</h1>
        <p>创建您的账号以使用问诊服务</p>
      </div>
      <a-form
        :model="formState"
        :rules="rules"
        @finish="onFinish"
        layout="vertical"
        class="register-form"
      >
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="formState.username"
            size="large"
            placeholder="3-20位字母数字下划线"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password
            v-model:value="formState.password"
            size="large"
            placeholder="6-20位字符"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password
            v-model:value="formState.confirmPassword"
            size="large"
            placeholder="请再次输入密码"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item label="姓名" name="name">
          <a-input
            v-model:value="formState.name"
            size="large"
            placeholder="请输入真实姓名"
          >
            <template #prefix>
              <IdcardOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item label="手机号" name="phone">
          <a-input
            v-model:value="formState.phone"
            size="large"
            placeholder="请输入手机号（可选）"
          >
            <template #prefix>
              <PhoneOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            注册
          </a-button>
        </a-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/patient/login">立即登录</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, IdcardOutlined, PhoneOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const router = useRouter();
const loading = ref(false);

const formState = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
});

const validateConfirmPassword = async (_rule: any, value: string) => {
  if (value && value !== formState.password) {
    throw new Error('两次密码输入不一致');
  }
};

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 20, message: '用户名长度为3-20位' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母数字下划线' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '密码长度为6-20位' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword },
  ],
  name: [
    { required: true, message: '请输入姓名' },
    { min: 2, max: 20, message: '姓名长度为2-20位' },
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
  ],
};

const onFinish = async () => {
  loading.value = true;

  try {
    const patient = await store.registerPatient({
      username: formState.username,
      password: formState.password,
      name: formState.name,
      phone: formState.phone,
    });

    if (patient) {
      message.success('注册成功');
      router.push('/');
    }
  } catch (error: any) {
    message.error(error.message || '注册失败，请重试');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.patient-register {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-container {
  background: #fff;
  border-radius: 16px;
  padding: 48px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.register-header p {
  font-size: 16px;
  color: #666;
}

.register-form {
  margin-bottom: 24px;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.register-footer a {
  color: #1890ff;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .register-container {
    margin: 24px;
    padding: 32px 24px;
  }

  .register-header h1 {
    font-size: 24px;
  }
}
</style>
