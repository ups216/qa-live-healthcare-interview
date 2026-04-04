<template>
  <div class="patient-login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>{{ isHomePage ? $t('patient.login.title') : '患者登录' }}</h2>
      </div>

      <a-form
        :model="formState"
        :rules="isHomePage ? rules : localRules"
        @finish="handleLogin"
        layout="vertical"
        class="login-form"
      >
        <a-form-item name="phone">
          <a-input
            v-model:value="formState.phone"
            :placeholder="isHomePage ? $t('patient.login.phone') : '请输入手机号'"
            size="large"
            autocomplete="off"
          >
            <template #prefix>
              <PhoneOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="formState.password"
            :placeholder="isHomePage ? $t('patient.login.password') : '请输入密码'"
            size="large"
            autocomplete="off"
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
            {{ isHomePage ? $t('patient.login.submit') : '登 录' }}
          </a-button>
        </a-form-item>

        <div class="login-footer">
          <a-button type="link" block @click="goToRegister">
            {{ isHomePage ? $t('patient.login.register') : '注册账号' }}
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import { PhoneOutlined, LockOutlined } from '@ant-design/icons-vue';
import { patientApi } from '../api/patient';
import { auth } from '../utils/auth';
import { useI18n } from 'vue-i18n';

const router = useRouter();
const route = useRoute();
const { t, tm } = useI18n();

const loading = ref(false);

// 判断是否在首页
const isHomePage = computed(() => route.path === '/' || route.query.from === 'home');

const formState = reactive({
  phone: '',
  password: '',
});

// 国际化验证规则
const rules = {
  phone: [
    { required: true, message: t('patient.validation.phoneRequired') },
    { pattern: /^1[3-9]\d{9}$/, message: t('patient.validation.phoneFormat') },
  ],
  password: [
    { required: true, message: t('patient.validation.passwordRequired') },
  ],
};

// 本地验证规则
const localRules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
  ],
  password: [
    { required: true, message: '请输入密码' },
  ],
};

const handleLogin = async () => {
  loading.value = true;
  try {
    const response = await patientApi.login(formState.phone, formState.password);
    auth.setTokens({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      expiresIn: response.expiresIn,
    });
    message.success(isHomePage.value ? t('patient.login.success') : '登录成功');
    const redirect = router.currentRoute.value.query.redirect as string;
    router.push(redirect || '/consultation');
  } catch (error: any) {
    message.error(error.message || (isHomePage.value ? t('patient.login.failed') : '登录失败，请检查手机号和密码'));
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  // 保持国际化来源标记
  const query = route.query.from ? { from: route.query.from } : {};
  router.push({ path: '/patient/register', query });
};
</script>

<style scoped>
.patient-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  margin-bottom: 16px;
}

.login-footer {
  text-align: center;
}

.login-links {
  margin-top: 16px;
  font-size: 14px;
}

.login-links a {
  color: #666;
  margin: 0 8px;
}
</style>
