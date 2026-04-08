<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>{{ t('header.logo') }}</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          {{ t('header.navHome') }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ t('header.navConsultation') }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ t('header.navDoctors') }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ t('header.navAbout') }}
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <a-dropdown :trigger="['click']">
          <a-button class="lang-switch-btn" @click.prevent>
            <GlobalOutlined /> {{ currentLangLabel }}
          </a-button>
          <template #overlay>
            <a-menu @click="handleLanguageChange">
              <a-menu-item key="zh">中文</a-menu-item>
              <a-menu-item key="en">English</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <!-- 患者登录态：已登录 -->
        <template v-if="isPatientLoggedIn">
          <span class="welcome-text">
            <UserOutlined /> {{ t('header.welcome', { name: patientName }) }}
          </span>
          <a-button class="logout-btn" @click="handlePatientLogout">{{ t('header.logout') }}</a-button>
        </template>

        <!-- 患者未登录态 -->
        <template v-else>
          <a-button type="primary" class="patient-login-btn" @click="navigateTo('/patient/login')">
            <UserOutlined />
            {{ t('header.patientLoginButton') }}
          </a-button>
        </template>

        <a-button class="doctor-login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          {{ t('header.loginButton') }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, GlobalOutlined } from '@ant-design/icons-vue';
import { store, PatientUser } from '../store';
import { message, Modal } from 'ant-design-vue';

const router = useRouter();
const route = useRoute();
const { t, locale } = useI18n();
const selectedKeys = ref<string[]>(['home']);

const currentLangLabel = computed(() => {
  return locale.value === 'zh' ? '中文' : 'EN';
});

// 患者登录态
const isPatientLoggedIn = computed(() => store.isPatientLoggedIn());
const patientName = computed(() => {
  const user: PatientUser | null = (store.state as any).currentPatientUser;
  return user?.name || '';
});

const handleLanguageChange = ({ key }: { key: string }) => {
  locale.value = key;
  localStorage.setItem('language', key);
};

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    selectedKeys.value = ['home'];
  } else if (newPath.startsWith('/consultation')) {
    selectedKeys.value = ['consultation'];
  } else if (newPath.startsWith('/doctors')) {
    selectedKeys.value = ['doctors'];
  } else if (newPath.startsWith('/about')) {
    selectedKeys.value = ['about'];
  }
}, { immediate: true });

const navigateTo = (path: string) => {
  router.push(path);
};

async function handlePatientLogout() {
  Modal.confirm({
    title: t('header.logoutConfirm'),
    okText: t('common.chinese') === '语言' ? '确定' : 'OK',
    cancelText: t('common.chinese') === '语言' ? '取消' : 'Cancel',
    onOk: async () => {
      try {
        const { logoutPatient: apiLogoutPatient } = await import('../api/patient');
        await apiLogoutPatient();
      } catch (e) {
        // 即使 API 调用失败，也要清除前端状态
      }
      store.logoutPatient();
      message.info(t('header.logoutSuccess'));
    },
  });
}
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo img {
  height: 40px;
  width: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.logo span {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  border: none;
  margin: 0 40px;
  line-height: 64px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-switch-btn {
  border-color: #d9d9d9;
  display: flex;
  align-items: center;
  gap: 6px;
}

.lang-switch-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

.patient-login-btn {
  background: #1890ff;
  border-color: #1890ff;
}

.patient-login-btn:hover {
  background: #40a9ff;
  border-color: #40a9ff;
}

.doctor-login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.doctor-login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

.welcome-text {
  color: #333;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 8px;
}

.logout-btn {
  color: #999;
  border-color: #d9d9d9;
}

.logout-btn:hover {
  color: #ff4d4f;
  border-color: #ff4d4f;
}
</style>
