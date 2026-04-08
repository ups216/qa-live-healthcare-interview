<template>
  <div class="login-page">
    <div class="login-card">
      <div class="card-header">
        <h2>{{ t('patient.login.title') }}</h2>
      </div>

      <!-- 已登录状态提示 -->
      <div v-if="isAlreadyLoggedIn" class="already-logged-in">
        <a-result status="success" :title="t('patient.login.alreadyLoggedIn')" :sub-title="t('patient.login.alreadyLoggedInSub')">
          <template #extra>
            <a-button type="primary" @click="$router.push('/consultation')">
              {{ t('patient.login.goToConsultation') }}
            </a-button>
            <a-button @click="handleLogout">
              {{ t('patient.login.logout') }}
            </a-button>
          </template>
        </a-result>
      </div>

      <!-- 登录表单 -->
      <a-form v-else :model="formState" @finish="handleLogin" layout="vertical" class="login-form">
        <a-alert
          v-if="errorMessage"
          :message="errorMessage"
          type="error"
          show-icon
          class="error-alert"
          closable
          @close="errorMessage = ''"
        />

        <a-form-item
          :label="t('patient.login.username')"
          name="username"
          :rules="[{ required: true, message: t('patient.login.usernameRequired') }]"
        >
          <a-input
            v-model:value="formState.username"
            :placeholder="t('patient.login.usernamePlaceholder')"
            size="large"
            allow-clear
          >
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>

        <a-form-item
          :label="t('patient.login.password')"
          name="password"
          :rules="[{ required: true, message: t('patient.login.passwordRequired') }]"
        >
          <a-input-password
            v-model:value="formState.password"
            :placeholder="t('patient.login.passwordPlaceholder')"
            size="large"
          >
            <template #prefix><LockOutlined /></template>
          </a-input-password>
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
            {{ loading ? t('patient.login.loggingIn') : t('patient.login.submit') }}
          </a-button>
        </a-form-item>

        <div class="login-footer">
          <span>{{ t('patient.login.noAccount') }}</span>
          <router-link to="/patient/register">{{ t('patient.login.registerNow') }}</router-link>
        </div>

        <div class="doctor-link">
          <router-link to="/doctor/login">{{ t('patient.login.doctorEntry') }}</router-link>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { store } from '../store';
import { loginPatient as apiLoginPatient } from '../api/patient';

const router = useRouter();
const { t } = useI18n();
const loading = ref(false);
const errorMessage = ref('');

const formState = reactive({
  username: '',
  password: '',
});

const isAlreadyLoggedIn = computed(() => store.isPatientLoggedIn());

onMounted(() => {
  // 如果已登录，显示已登录提示
});

async function handleLogin() {
  loading.value = true;
  errorMessage.value = '';
  try {
    const user = await apiLoginPatient(formState.username, formState.password);
    store.loginPatient(user);
    message.success(t('patient.login.success', { name: user.name }));
    const redirect = (router.currentRoute.value.query.redirect as string) || '/';
    router.push(redirect);
  } catch (e: any) {
    errorMessage.value = e.message || t('patient.login.failed');
  } finally {
    loading.value = false;
  }
}

function handleLogout() {
  store.logoutPatient();
  message.info(t('patient.login.logoutSuccess'));
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  margin-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
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

.login-form {
  padding: 24px 28px 20px;
}

.error-alert {
  margin-bottom: 16px;
}

.submit-btn {
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 12px;
  color: #888;
  font-size: 14px;
}

.login-footer a {
  color: #1890ff;
  margin-left: 4px;
  font-weight: 500;
}

.doctor-link {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.doctor-link a {
  color: #999;
  font-size: 13px;
}
</style>
