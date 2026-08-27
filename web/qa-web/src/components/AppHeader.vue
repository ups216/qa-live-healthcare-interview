<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo" @click="navigateTo('/')">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>QA Live Healthcare</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          {{ t('header.home') }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ t('header.consultation') }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ t('header.doctors') }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ t('header.about') }}
        </a-menu-item>
      </a-menu>
      <div class="header-right">
        <a-dropdown>
          <a-button class="lang-btn">
            <GlobalOutlined />
            {{ currentLangLabel }}
          </a-button>
          <template #overlay>
            <a-menu @click="handleLanguageChange">
              <a-menu-item key="zh-CN">
                中文
              </a-menu-item>
              <a-menu-item key="en-US">
                English
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <template v-if="currentPatient">
          <a-dropdown>
            <a-button class="user-btn">
              <UserOutlined />
              {{ currentPatient.name }}
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="handlePatientLogout">
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <a-button class="patient-login-btn" @click="navigateTo('/patient/login')">
            <UserOutlined />
            患者登录
          </a-button>
        </template>

        <a-button type="primary" class="login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          {{ t('header.doctorLogin') }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { message } from 'ant-design-vue';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, GlobalOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const router = useRouter();
const route = useRoute();
const { t, locale } = useI18n();
const selectedKeys = ref<string[]>(['home']);

const currentLangLabel = computed(() => {
  return locale.value === 'zh-CN' ? '中文' : 'English';
});

const currentPatient = computed(() => store.state.currentPatient);

onMounted(() => {
  store.restorePatient();
});

const handleLanguageChange = ({ key }: { key: string }) => {
  locale.value = key;
  localStorage.setItem('locale', key);
};

const handlePatientLogout = () => {
  store.logoutPatient();
  message.success('已退出登录');
  router.push('/');
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.patient-login-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #52c41a;
  border-color: #52c41a;
  color: #fff;
}

.patient-login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
  color: #fff;
}

.login-btn {
  background: #1890ff;
  border-color: #1890ff;
}

.login-btn:hover {
  background: #40a9ff;
  border-color: #40a9ff;
}
</style>
