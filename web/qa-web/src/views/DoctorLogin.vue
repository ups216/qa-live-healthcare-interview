<template>
  <div class="doctor-login">
    <!-- Language Switcher -->
    <div class="language-switcher">
      <a-dropdown>
        <a-button type="text" class="lang-btn">
          <GlobalOutlined />
          {{ currentLanguageLabel }}
          <DownOutlined />
        </a-button>
        <template #overlay>
          <a-menu @click="handleLanguageChange">
            <a-menu-item key="zh-CN">
              <span>中文</span>
            </a-menu-item>
            <a-menu-item key="en-US">
              <span>English</span>
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <div class="login-container">
      <div class="login-header">
        <h1>{{ t('doctorLogin.title') }}</h1>
        <p>{{ t('doctorLogin.subtitle') }}</p>
      </div>
      <a-form
        :model="formState"
        :rules="rules"
        @finish="onFinish"
        layout="vertical"
        class="login-form"
      >
        <a-form-item :label="t('doctorLogin.username')" name="username">
          <a-input
            v-model:value="formState.username"
            size="large"
            :placeholder="t('doctorLogin.usernamePlaceholder')"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item :label="t('doctorLogin.password')" name="password">
          <a-input-password
            v-model:value="formState.password"
            size="large"
            :placeholder="t('doctorLogin.passwordPlaceholder')"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            {{ t('doctorLogin.loginButton') }}
          </a-button>
        </a-form-item>
      </a-form>

      <a-alert
        :message="t('doctorLogin.testAccount.title')"
        :description="t('doctorLogin.testAccount.description')"
        type="info"
        show-icon
        closable
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, GlobalOutlined, DownOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const router = useRouter();
const { t, locale } = useI18n();
const loading = ref(false);

const currentLanguageLabel = computed(() => {
  return locale.value === 'zh-CN' ? '中文' : 'English';
});

const handleLanguageChange = ({ key }: { key: string }) => {
  locale.value = key;
  localStorage.setItem('locale', key);
};

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

  setTimeout(() => {
    const doctor = store.loginDoctor(formState.username, formState.password);

    if (doctor) {
      message.success('登录成功');
      router.push(`/doctor/room/${doctor.username}`);
    } else {
      message.error('用户名或密码错误');
    }

    loading.value = false;
  }, 500);
};
</script>

<style scoped>
.doctor-login {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.language-switcher {
  position: fixed;
  top: 80px;
  right: 24px;
  z-index: 1000;
}

.lang-btn {
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.lang-btn:hover {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
