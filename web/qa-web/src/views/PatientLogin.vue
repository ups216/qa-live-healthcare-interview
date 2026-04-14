<template>
  <div class="patient-login">
    <div class="login-container">
      <div class="login-header">
        <h1>患者登录</h1>
        <p>请使用您的患者账号登录系统</p>
      </div>
      <a-form
        :model="formState"
        :rules="rules"
        @finish="onFinish"
        layout="vertical"
        class="login-form"
      >
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="formState.username"
            size="large"
            placeholder="请输入用户名"
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
            placeholder="请输入密码"
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

        <div class="login-footer">
          <a-button type="link" @click="goToConsultation">
            直接进入问诊
          </a-button>
          <a-divider type="vertical" />
          <a-button type="link" @click="goToRegister">
            注册新账户
          </a-button>
        </div>
      </a-form>

      <a-alert
        message="测试账号提示"
        description="用户名: patient001, 密码: 123456"
        type="info"
        show-icon
        closable
      />
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

const formState = reactive({
  username: '',
  password: '',
});

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
};

const onFinish = async () => {
  loading.value = true;

  try {
    const loginResult = await store.loginPatient(formState.username, formState.password);

    if (loginResult && loginResult.success) {
      message.success('登录成功');
      // 登录成功后跳转到问诊页面
      router.push('/consultation');
    } else {
      message.error(loginResult?.message || '用户名或密码错误');
    }
  } catch (error) {
    console.error('登录错误:', error);
    message.error('登录失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const goToConsultation = () => {
  router.push('/consultation');
};

const goToRegister = () => {
  message.info('患者注册功能开发中');
};
</script>

<style scoped>
.patient-login {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  background: #fff;
  border-radius: 16px;
  padding: 48px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 16px;
  color: #666;
}

.login-form {
  margin-bottom: 24px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer .ant-btn-link {
  color: #1890ff;
  font-size: 14px;
}

@media (max-width: 768px) {
  .login-container {
    margin: 24px;
    padding: 32px 24px;
  }

  .login-header h1 {
    font-size: 24px;
  }
}
</style>