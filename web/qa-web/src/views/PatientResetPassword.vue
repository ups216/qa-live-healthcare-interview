<template>
  <div class="reset-password-container">
    <div class="reset-password-form-wrapper">
      <div class="reset-password-header">
        <h1>{{ t('patientResetPassword.title') }}</h1>
        <p>{{ t('patientResetPassword.subtitle') }}</p>
      </div>
      <a-form
        :model="formState"
        layout="vertical"
        :label-col="{ span: 24 }"
        :wrapper-col="{ span: 24 }"
      >
        <a-form-item :label="t('patientResetPassword.name')" :validate-status="nameError ? 'error' : ''" :help="nameError || ''">
          <a-input
            v-model:value="formState.name"
            :placeholder="t('patientResetPassword.namePlaceholder')"
            @blur="validateName"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item :label="t('patientResetPassword.birthday')" :validate-status="birthdayError ? 'error' : ''" :help="birthdayError || ''">
          <a-date-picker
            v-model:value="birthday"
            format="YYYY-MM-DD"
            :placeholder="t('patientResetPassword.birthdayPlaceholder')"
            @change="handleBirthdayChange"
          />
        </a-form-item>

        <a-form-item :label="t('patientResetPassword.newPassword')" :validate-status="passwordError ? 'error' : ''" :help="passwordError || ''">
          <a-input-password
            v-model:value="formState.newPassword"
            :placeholder="t('patientResetPassword.passwordPlaceholder')"
            @blur="validatePassword"
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
            class="reset-button"
            :loading="loading"
            @click="onFinish"
          >
            {{ t('patientResetPassword.reset') }}
          </a-button>
          <div class="reset-footer">
            <a @click="navigateTo('/patient/login')" class="back-to-login">
              {{ t('patientResetPassword.backToLogin') }}
            </a>
          </div>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import type { Dayjs } from 'dayjs';
import { store } from '../store';
import { t } from '../locales';

const router = useRouter();

const formState = ref({
  name: '',
  birthday: '',
  newPassword: '',
});

const birthday = ref<Dayjs | null>(null);
const nameError = ref('');
const birthdayError = ref('');
const passwordError = ref('');
const loading = ref(false);

const validateName = () => {
  if (!formState.value.name) {
    nameError.value = t('patientResetPassword.nameRequired');
  } else {
    nameError.value = '';
  }
};

const validatePassword = () => {
  if (!formState.value.newPassword) {
    passwordError.value = t('patientResetPassword.passwordRequired');
  } else {
    passwordError.value = '';
  }
};

const handleBirthdayChange = (date: Dayjs | null) => {
  if (date) {
    formState.value.birthday = date.format('YYYY-MM-DD');
    birthdayError.value = '';
  } else {
    formState.value.birthday = '';
    birthdayError.value = t('patientResetPassword.birthdayRequired');
  }
};

const onFinish = async () => {
  validateName();
  validatePassword();

  if (!formState.value.birthday) {
    birthdayError.value = t('patientResetPassword.birthdayRequired');
  } else {
    birthdayError.value = '';
  }

  if (nameError.value || birthdayError.value || passwordError.value) {
    return;
  }

  loading.value = true;

  try {
    const result = await store.resetPassword(
      formState.value.name,
      formState.value.birthday,
      formState.value.newPassword
    );

    if (result.success) {
      message.success(t('patientResetPassword.resetSuccess'));
      router.push('/patient/login');
    } else {
      message.error(result.message || t('patientResetPassword.resetFailed'));
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
.reset-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-password-form-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.reset-password-header {
  text-align: center;
  margin-bottom: 30px;
}

.reset-password-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1890ff;
}

.reset-password-header p {
  color: #666;
  font-size: 14px;
}

.reset-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  margin-bottom: 16px;
  background: #52c41a;
  border-color: #52c41a;
}

.reset-button:hover {
  background: #73d13d !important;
  border-color: #73d13d !important;
}

.reset-footer {
  text-align: right;
}

.back-to-login {
  color: #1890ff;
  font-size: 14px;
}

.back-to-login:hover {
  text-decoration: underline;
}
</style>
