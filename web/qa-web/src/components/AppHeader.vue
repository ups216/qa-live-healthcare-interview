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
          {{ $t('nav.home') }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ $t('nav.consultation') }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ $t('nav.doctors') }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ $t('nav.about') }}
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <!-- 语言切换下拉框 -->
        <a-select
          v-model:value="currentLanguage"
          class="language-select"
          @change="handleLanguageChange"
          :bordered="false"
        >
          <a-select-option value="zh-CN">
            <span class="language-option">中文</span>
          </a-select-option>
          <a-select-option value="en-US">
            <span class="language-option">English</span>
          </a-select-option>
        </a-select>
        <a-button type="primary" class="login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          {{ $t('nav.doctorLogin') }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined } from '@ant-design/icons-vue';
import { setLanguage, getCurrentLanguage } from '../i18n';

const router = useRouter();
const route = useRoute();
const selectedKeys = ref<string[]>(['home']);
const currentLanguage = ref<string>(getCurrentLanguage());

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

// 处理语言切换
const handleLanguageChange = (value: string) => {
  setLanguage(value);
  currentLanguage.value = value;
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.language-select {
  width: 100px;
}

.language-select :deep(.ant-select-selector) {
  background: transparent !important;
  color: #666;
  font-size: 14px;
}

.language-select :deep(.ant-select-arrow) {
  color: #666;
}

.language-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}
</style>
