<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>QA Live Healthcare</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          {{ $t('header.home') }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ $t('header.consultation') }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ $t('header.doctors') }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ $t('header.about') }}
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <a-dropdown>
          <a-button type="text" class="lang-btn">
            <GlobalOutlined />
            {{ currentLocaleName }}
          </a-button>
          <template #overlay>
            <a-menu @click="handleLocaleChange">
              <a-menu-item key="zh-CN">中文</a-menu-item>
              <a-menu-item key="en-US">English</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-dropdown v-if="isPatientLoggedIn">
          <a-button type="primary" class="patient-btn">
            <UserOutlined />
            {{ currentPatientName }}
          </a-button>
          <template #overlay>
            <a-menu>
              <a-menu-item key="profile" @click="viewProfile">个人中心</a-menu-item>
              <a-menu-item key="logout" @click="handleLogout">{{ $t('header.patientLogout') }}</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-button v-else type="primary" class="login-btn" @click="goToPatientLogin">
          <UserOutlined />
          {{ $t('header.patientLogin') }}
        </a-button>
        <a-button class="doctor-login-btn" @click="navigateTo('/doctor/login')">
          {{ $t('header.login') }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, GlobalOutlined } from '@ant-design/icons-vue';
import type { MenuProps } from 'ant-design-vue';
import { localeNames, type Locale } from '../locales';
import { auth } from '../utils/auth';
import { patientApi } from '../api/patient';

const router = useRouter();
const route = useRoute();
const { locale } = useI18n();
const selectedKeys = ref<string[]>(['home']);

const isPatientLoggedIn = ref(false);
const currentPatientName = ref('');

const currentLocaleName = computed(() => localeNames[locale.value as Locale]);

const checkPatientLogin = () => {
  isPatientLoggedIn.value = auth.isLoggedIn();
};

const loadPatientProfile = async () => {
  if (isPatientLoggedIn.value) {
    try {
      const profile = await patientApi.getProfile();
      currentPatientName.value = profile.name;
    } catch (error) {
      console.error('Failed to load patient profile:', error);
    }
  }
};

const handleLogout = async () => {
  try {
    await patientApi.logout();
  } catch (error) {
    console.error('Logout API failed:', error);
  }
  auth.clearTokens();
  isPatientLoggedIn.value = false;
  currentPatientName.value = '';
  // 退出登录后跳转到首页
  router.push('/');
};

const viewProfile = () => {
  router.push('/patient/profile');
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
  // 路由变化时重新检查登录状态
  checkPatientLogin();
  loadPatientProfile();
}, { immediate: true });

const navigateTo = (path: string) => {
  router.push(path);
};

// 跳转到患者登录页，根据是否在首页决定是否使用国际化
const goToPatientLogin = () => {
  const isHome = route.path === '/';
  router.push({
    path: '/patient/login',
    query: isHome ? { from: 'home' } : {}
  });
};

const handleLocaleChange: MenuProps['onClick'] = ({ key }) => {
  locale.value = key as Locale;
};

onMounted(() => {
  checkPatientLogin();
  loadPatientProfile();
});
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
  gap: 16px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

.patient-btn {
  background: #1890ff;
  border-color: #1890ff;
}

.patient-btn:hover {
  background: #40a9ff;
  border-color: #40a9ff;
}

.doctor-login-btn {
  background: transparent;
  border-color: #d9d9d9;
  color: #666;
}

.doctor-login-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}
</style>
