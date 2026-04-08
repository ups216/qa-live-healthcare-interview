<template>
  <div class="register-page">
    <div class="register-card">
      <div class="card-header">
        <h2>{{ t('patient.register.title') }}</h2>
      </div>

      <!-- 已登录状态提示 -->
      <div v-if="isAlreadyLoggedIn" class="already-logged-in">
        <a-result status="success" :title="t('patient.register.alreadyRegistered')" :sub-title="t('patient.register.alreadyRegisteredSub')">
          <template #extra>
            <a-button type="primary" @click="$router.push('/consultation')">
              {{ t('patient.register.goToConsultation') }}
            </a-button>
            <a-button @click="store.logoutPatient()">
              {{ t('patient.register.logout') }}
            </a-button>
          </template>
        </a-result>
      </div>

      <!-- 注册表单 -->
      <a-form
        v-else
        :model="formState"
        @finish="handleRegister"
        layout="vertical"
        class="register-form"
      >
        <a-alert
          v-if="errorMessage"
          :message="errorMessage"
          type="error"
          show-icon
          closable
          class="error-alert"
          @close="errorMessage = ''"
        />

        <!-- 用户名 -->
        <a-form-item
          :label="t('patient.register.username')"
          name="username"
          :rules="[
            { required: true, message: t('patient.register.usernameRequired') },
            { min: 3, max: 20, message: t('patient.register.usernameLength') },
            { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: t('patient.register.usernamePattern') }
          ]"
        >
          <a-input
            v-model:value="formState.username"
            :placeholder="t('patient.register.usernamePlaceholder')"
            size="large"
            allow-clear
            @blur="checkUsernameAvailability"
          >
            <template #prefix><UserOutlined /></template>
            <template #suffix>
              <span v-if="usernameChecking" style="color:#1890ff">...</span>
              <span v-else-if="usernameChecked && !usernameAvailable" style="color:#ff4d4f">&#10005;</span>
              <span v-else-if="usernameChecked && usernameAvailable" style="color:#52c41a">&#10003;</span>
            </template>
          </a-input>
        </a-form-item>

        <!-- 密码 -->
        <a-form-item
          :label="t('patient.register.password')"
          name="password"
          :rules="[
            { required: true, message: t('patient.register.passwordRequired') },
            { min: 6, max: 20, message: t('patient.register.passwordLength') },
            { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: t('patient.register.passwordPattern') }
          ]"
        >
          <a-input-password
            v-model:value="formState.password"
            :placeholder="t('patient.register.passwordPlaceholder')"
            size="large"
            @change="updatePasswordStrength"
          >
            <template #prefix><LockOutlined /></template>
          </a-input-password>
          <div v-if="formState.password" class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="'strength-' + passwordStrength.level"
                :style="{ width: passwordStrength.percent + '%' }"
              ></div>
            </div>
            <span class="strength-text" :class="'text-' + passwordStrength.level">
              {{ t(`patient.register.strength${passwordStrength.label}`) }}
            </span>
          </div>
        </a-form-item>

        <!-- 确认密码 -->
        <a-form-item
          :label="t('patient.register.confirmPassword')"
          name="confirmPassword"
          :rules="[
            { required: true, message: t('patient.register.confirmRequired') },
            { validator: validateConfirmPassword }
          ]"
        >
          <a-input-password
            v-model:value="formState.confirmPassword"
            :placeholder="t('patient.register.confirmPlaceholder')"
            size="large"
          />
        </a-form-item>

        <!-- 姓名 -->
        <a-form-item
          :label="t('patient.register.name')"
          name="name"
          :rules="[
            { required: true, message: t('patient.register.nameRequired') },
            { min: 2, max: 10, message: t('patient.register.nameLength') }
          ]"
        >
          <a-input
            v-model:value="formState.name"
            :placeholder="t('patient.register.namePlaceholder')"
            size="large"
            allow-clear
          />
        </a-form-item>

        <!-- 性别 + 生日（一行） -->
        <div class="row-inline">
          <a-form-item
            :label="t('patient.register.gender')"
            name="gender"
            :rules="[{ required: true, message: t('patient.register.genderRequired') }]"
            class="half-width"
          >
            <a-select v-model:value="formState.gender" :placeholder="t('patient.register.genderPlaceholder')" size="large">
              <a-select-option value="男">{{ t('patient.register.male') }}</a-select-option>
              <a-select-option value="女">{{ t('patient.register.female') }}</a-select-option>
              <a-select-option value="其他">{{ t('patient.register.other') }}</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item
            :label="t('patient.register.birthday')"
            name="birthday"
            :rules="[{ required: true, message: t('patient.register.birthdayRequired') }]"
            class="half-width"
          >
            <a-date-picker
              v-model:value="formState.birthday"
                style="width: 100%"
                size="large"
                format="YYYY-MM-DD"
                :disabled-date="disableFutureDates"
              />
          </a-form-item>
        </div>

        <!-- 手机号（可选） -->
        <a-form-item
          :label="t('patient.register.phone')"
          name="phone"
          :rules="[
            { pattern: /^1[3-9]\d{9}$/, message: t('patient.register.phoneFormat') }
          ]"
        >
          <a-input
            v-model:value="formState.phone"
            :placeholder="t('patient.register.phonePlaceholder')"
            size="large"
            allow-clear
          >
            <template #prefix><PhoneOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            block
            size="large"
            :loading="loading"
            class="submit-btn"
          >
            {{ loading ? t('patient.register.registering') : t('patient.register.submit') }}
          </a-button>
        </a-form-item>

        <div class="register-footer">
          <span>{{ t('patient.register.hasAccount') }}</span>
          <router-link to="/patient/login">{{ t('patient.register.goToLogin') }}</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import dayjs, { Dayjs } from 'dayjs';
import { useI18n } from 'vue-i18n';
import { UserOutlined, LockOutlined, PhoneOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { store } from '../store';
import { registerPatient as apiRegisterPatient, checkUsername as apiCheckUsername } from '../api/patient';

const router = useRouter();
const { t } = useI18n();
const loading = ref(false);
const errorMessage = ref('');
const usernameChecking = ref(false);
const usernameChecked = ref(false);
const usernameAvailable = ref(false);

interface FormState {
  username: string;
  password: string;
  confirmPassword: string;
  name: string;
  gender: string;
  birthday: Dayjs | null;
  phone: string;
}

const formState = reactive<FormState>({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '',
  birthday: null,
  phone: '',
});

const isAlreadyLoggedIn = computed(() => store.isPatientLoggedIn());

// 密码强度计算
const passwordStrength = computed(() => {
  const pwd = formState.password;
  if (!pwd) return { level: 0, label: '', percent: 0 };
  let score = 0;
  if (pwd.length >= 6) score += 20;
  if (pwd.length >= 10) score += 20;
  if (/[a-z]/.test(pwd)) score += 15;
  if (/[A-Z]/.test(pwd)) score += 15;
  if (/\d/.test(pwd)) score += 15;
  if (/[^a-zA-Z\d]/.test(pwd)) score += 15;
  if (score <= 30) return { level: 1, label: 'Weak', percent: 33 };
  if (score <= 60) return { level: 2, label: 'Medium', percent: 66 };
  return { level: 3, label: 'Strong', percent: 100 };
});

function updatePasswordStrength() {
  // 响应式自动更新，无需额外操作
}

function disableFutureDates(current: Dayjs) {
  return current && current > dayjs().endOf('day');
}

async function checkUsernameAvailability() {
  const uname = formState.username.trim();
  if (!uname || uname.length < 3) return;
  usernameChecking.value = true;
  usernameChecked.value = false;
  try {
    const result = await apiCheckUsername(uname);
    usernameAvailable.value = result.available;
    usernameChecked.value = true;
    if (!result.available) {
      errorMessage.value = t('patient.register.usernameTaken');
    }
  } catch (e) {
    // 静默失败，不阻断用户输入
  } finally {
    usernameChecking.value = false;
  }
}

function validateConfirmPassword(_rule: any, value: string) {
  if (value && value !== formState.password) {
    return Promise.reject(t('patient.register.confirmMismatch'));
  }
  return Promise.resolve();
}

async function handleRegister() {
  loading.value = true;
  errorMessage.value = '';
  try {
    const user = await apiRegisterPatient({
      username: formState.username,
      password: formState.password,
      confirmPassword: formState.confirmPassword,
      name: formState.name,
      gender: formState.gender,
      birthday: formState.birthday ? formState.birthday.format('YYYY-MM-DD') : '',
      phone: formState.phone || undefined,
    });
    store.loginPatient(user);
    message.success(t('patient.register.success', { name: user.name }));
    router.push('/');
  } catch (e: any) {
    errorMessage.value = e.message || t('patient.register.failed');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 20px;
  margin-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-y: auto;
}

.register-card {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  margin: 20px 0;
  overflow: hidden;
}

.card-header {
  text-align: center;
  padding: 28px 24px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  margin: 0;
  font-size: 22px;
  color: #1890ff;
  font-weight: 600;
}

.already-logged-in {
  padding: 32px 24px;
}

.register-form {
  padding: 24px 28px 20px;
}

.error-alert {
  margin-bottom: 16px;
}

.row-inline {
  display: flex;
  gap: 16px;
}

.half-width {
  flex: 1;
  margin-bottom: 16px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-strength-1 { background-color: #ff4d4f; }
.strength-strength-2 { background-color: #faad14; }
.strength-strength-3 { background-color: #52c41a; }

.strength-text {
  font-size: 12px;
  white-space: nowrap;
}

.text-1 { color: #ff4d4f; }
.text-2 { color: #faad14; }
.text-3 { color: #52c41a; }

.submit-btn {
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 8px;
}

.register-footer {
  text-align: center;
  margin-top: 12px;
  color: #888;
  font-size: 14px;
}

.register-footer a {
  color: #1890ff;
  margin-left: 4px;
  font-weight: 500;
}
</style>
