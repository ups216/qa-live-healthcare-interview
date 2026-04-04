<template>
  <div class="patient-register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>{{ isHomePage ? $t('patient.register.title') : '患者注册' }}</h2>
      </div>

      <a-form
        :model="formState"
        :rules="isHomePage ? rules : localRules"
        @finish="handleRegister"
        layout="vertical"
        class="register-form"
      >
        <a-form-item name="phone">
          <a-input
            v-model:value="formState.phone"
            :placeholder="isHomePage ? $t('patient.register.phone') : '请输入手机号'"
            size="large"
            :disabled="countdown > 0"
          >
            <template #prefix>
              <PhoneOutlined />
            </template>
            <template #suffix>
              <a-button
                type="link"
                size="small"
                :disabled="countdown > 0 || formState.phone.length !== 11"
                @click="sendCode"
              >
                {{ countdown > 0 ? `${countdown}s` : (isHomePage ? $t('patient.register.sendCode') : '获取验证码') }}
              </a-button>
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="code">
          <a-input
            v-model:value="formState.code"
            :placeholder="isHomePage ? $t('patient.register.code') : '请输入验证码'"
            size="large"
            maxlength="6"
          >
            <template #prefix>
              <SafetyOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="formState.password"
            :placeholder="isHomePage ? $t('patient.register.password') : '设置密码 (8-20位，字母+数字)'"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="confirmPassword">
          <a-input-password
            v-model:value="formState.confirmPassword"
            :placeholder="isHomePage ? $t('patient.register.confirmPassword') : '确认密码'"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="name">
          <a-input
            v-model:value="formState.name"
            :placeholder="isHomePage ? $t('patient.register.name') : '请输入姓名'"
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="gender">
          <a-select
            v-model:value="formState.gender"
            :placeholder="isHomePage ? $t('patient.register.gender') : '请选择性别'"
            size="large"
          >
            <a-select-option :value="0">{{ isHomePage ? $t('patient.register.genderUnknown') : '未知' }}</a-select-option>
            <a-select-option :value="1">{{ isHomePage ? $t('patient.register.genderMale') : '男' }}</a-select-option>
            <a-select-option :value="2">{{ isHomePage ? $t('patient.register.genderFemale') : '女' }}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item name="birthday">
          <a-date-picker
            v-model:value="formState.birthday"
            :placeholder="isHomePage ? $t('patient.register.birthday') : '请选择生日'"
            size="large"
            format="YYYY-MM-DD"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            {{ isHomePage ? $t('patient.register.submit') : '注 册' }}
          </a-button>
        </a-form-item>

        <div class="register-footer">
          <a-button type="link" @click="goToLogin">
            {{ isHomePage ? $t('patient.register.login') : '已有账户？登录' }}
          </a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onUnmounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import { PhoneOutlined, LockOutlined, SafetyOutlined, UserOutlined } from '@ant-design/icons-vue';
import { patientApi } from '../api/patient';
import { auth } from '../utils/auth';
import { useI18n } from 'vue-i18n';

const router = useRouter();
const route = useRoute();
const { t } = useI18n();

const loading = ref(false);
const countdown = ref(0);
let countdownTimer: number | null = null;

// 判断是否在首页
const isHomePage = computed(() => route.path === '/' || route.query.from === 'home');

const formState = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: undefined as number | undefined,
  birthday: null as dayjs.Dayjs | null,
});

// 国际化验证规则
const validatePhone = async (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject(t('patient.validation.phoneRequired'));
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    return Promise.reject(t('patient.validation.phoneFormat'));
  }
  return Promise.resolve();
};

const validatePassword = (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject(t('patient.validation.passwordRequired'));
  }
  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/.test(value)) {
    return Promise.reject(t('patient.validation.passwordFormat'));
  }
  return Promise.resolve();
};

const validateConfirmPassword = (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject(t('patient.validation.confirmPasswordRequired'));
  }
  if (value !== formState.password) {
    return Promise.reject(t('patient.validation.passwordMismatch'));
  }
  return Promise.resolve();
};

const rules = {
  phone: [{ required: true, validator: validatePhone, trigger: 'change' }],
  code: [
    { required: true, message: t('patient.validation.codeRequired') },
    { len: 6, message: t('patient.validation.codeLength') },
  ],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  name: [
    { required: true, message: t('patient.validation.nameRequired') },
    { min: 2, max: 20, message: t('patient.validation.nameLength') },
  ],
};

// 本地验证规则
const validatePhoneLocal = async (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject('请输入手机号');
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    return Promise.reject('请输入正确的手机号');
  }
  return Promise.resolve();
};

const validatePasswordLocal = (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject('请输入密码');
  }
  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/.test(value)) {
    return Promise.reject('密码必须为8-20位，包含字母和数字');
  }
  return Promise.resolve();
};

const validateConfirmPasswordLocal = (_rule: any, value: string) => {
  if (!value) {
    return Promise.reject('请确认密码');
  }
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
};

const localRules = {
  phone: [{ required: true, validator: validatePhoneLocal, trigger: 'change' }],
  code: [
    { required: true, message: '请输入验证码' },
    { len: 6, message: '验证码为6位数字' },
  ],
  password: [{ required: true, validator: validatePasswordLocal, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPasswordLocal, trigger: 'blur' }],
  name: [
    { required: true, message: '请输入姓名' },
    { min: 2, max: 20, message: '姓名为2-20个字符' },
  ],
};

const sendCode = async () => {
  if (formState.phone.length !== 11) {
    message.error(isHomePage.value ? t('patient.validation.phoneFormat') : '请输入正确的手机号');
    return;
  }

  try {
    const result = await patientApi.sendSmsCode(formState.phone, 'register');
    message.success(isHomePage.value ? t('patient.register.codeSent') : '验证码已发送');
    countdown.value = Math.floor(result.expiresIn / 1000);
    countdownTimer = window.setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer);
        countdownTimer = null;
      }
    }, 1000);
  } catch (error: any) {
    message.error(error.message || (isHomePage.value ? t('patient.register.codeFailed') : '验证码发送失败'));
  }
};

const handleRegister = async () => {
  loading.value = true;
  try {
    const response = await patientApi.register({
      phone: formState.phone,
      code: formState.code,
      password: formState.password,
      name: formState.name,
      gender: formState.gender,
      birthday: formState.birthday?.format('YYYY-MM-DD'),
    });

    auth.setTokens({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      expiresIn: response.expiresIn,
    });

    message.success(isHomePage.value ? t('patient.register.success') : '注册成功');
    router.push('/consultation');
  } catch (error: any) {
    message.error(error.message || (isHomePage.value ? t('patient.register.failed') : '注册失败，请重试'));
  } finally {
    loading.value = false;
  }
};

const goToLogin = () => {
  // 保持国际化来源标记
  const query = route.query.from ? { from: route.query.from } : {};
  router.push({ path: '/patient/login', query });
};

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.patient-register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 100%;
  max-width: 420px;
  padding: 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.register-form {
  margin-bottom: 16px;
}

.register-footer {
  text-align: center;
}
</style>
