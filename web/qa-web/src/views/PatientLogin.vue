<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h1>{{ t('patientLogin.title') }}</h1>
        <p>{{ t('patientLogin.subtitle') }}</p>
      </div>
      <a-form
        :model="formState"
        layout="vertical"
        :label-col="{ span: 24 }"
        :wrapper-col="{ span: 24 }"
      >
        <a-form-item :label="t('patientLogin.name')" :validate-status="nameError ? 'error' : ''" :help="nameError || ''">
          <a-input
            v-model:value="formState.name"
            :placeholder="t('patientLogin.namePlaceholder')"
            @blur="validateName"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item :label="t('patientLogin.password')" :validate-status="passwordError ? 'error' : ''" :help="passwordError || ''">
          <a-input-password
            v-model:value="formState.password"
            :placeholder="t('patientLogin.passwordPlaceholder')"
            @blur="validatePassword"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-checkbox v-model:checked="formState.remember">
            {{ t('patientLogin.remember') }}
          </a-checkbox>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            class="login-button"
            :loading="loading"
            @click="onFinish"
          >
            {{ t('patientLogin.login') }}
          </a-button>
          <div class="login-footer">
            <a @click="navigateTo('/patient/reset-password')" class="forgot-password">
              {{ t('patientLogin.forgotPassword') }}
            </a>
          </div>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { store } from '../store';
import { t } from '../locales';

const router = useRouter();

const formState = ref({
  name: '',
  password: '',
  remember: false,
});

const nameError = ref('');
const passwordError = ref('');
const loading = ref(false);

const validateName = () => {
  if (!formState.value.name) {
    nameError.value = t('patientLogin.nameRequired');
  } else {
    nameError.value = '';
  }
};

const validatePassword = () => {
  if (!formState.value.password) {
    passwordError.value = t('patientLogin.passwordRequired');
  } else {
    passwordError.value = '';
  }
};

const onFinish = async () => {
  validateName();
  validatePassword();

  if (nameError.value || passwordError.value) {
    return;
  }

  loading.value = true;

  try {
    const result = await store.loginPatient(formState.value.name, formState.value.password);

    if (result.success && result.patient) {
      message.success(t('patientLogin.loginSuccess'));
      router.push(`/patient/room/${result.patient.name}`);
    } else {
      message.error(result.message || t('patientLogin.loginFailed'));
    }
  } finally {
    loading.value = false;
  }
};

const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-form-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1890ff;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  margin-bottom: 16px;
  background: #52c41a;
  border-color: #52c41a;
}

.login-button:hover {
  background: #73d13d !important;
  border-color: #73d13d !important;
}

.login-footer {
  text-align: right;
}

.forgot-password {
  color: #1890ff;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}
</style>
