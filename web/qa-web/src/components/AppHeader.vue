<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>{{ t.app.title }}</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          {{ t.app.home }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ t.app.consultation }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ t.app.doctors }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ t.app.about }}
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <a-select
          v-model:value="currentLocale"
          :options="availableLocales"
          size="small"
          class="language-selector"
          @change="handleLanguageChange"
        />
        <div v-if="userStore.state.isLoggedIn" class="user-info">
          <a-dropdown :trigger="['click']">
            <a-button type="text" class="user-info-btn">
              <UserOutlined />
              {{ userStore.state.user?.name || userStore.state.user?.username }}
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else class="login-buttons">
          <a-button type="primary" class="user-login-btn" @click="showUserLoginModal">
            <UserOutlined />
            用户登录
          </a-button>
          <a-button type="primary" class="doctor-login-btn" @click="navigateTo('/doctor/login')">
            <UserOutlined />
            {{ t.app.doctorLogin }}
          </a-button>
        </div>
      </div>
    </div>
    <UserLoginModal
      :visible="userLoginModalVisible"
      @update:visible="(value) => userLoginModalVisible = value"
      @success="handleUserLoginSuccess"
      @cancel="handleUserLoginCancel"
    />
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { useI18n, type Locale } from '../composables/useI18n';
import UserLoginModal from './UserLoginModal.vue';
import { userStore } from '../store/userStore';

const router = useRouter();
const route = useRoute();
const selectedKeys = ref<string[]>(['home']);

const { t, locale, setLocale, availableLocales } = useI18n();
const currentLocale = ref<Locale>(locale.value);

const userLoginModalVisible = ref(false);

const showUserLoginModal = () => {
  userLoginModalVisible.value = true;
};

const handleUserLoginSuccess = () => {
  userLoginModalVisible.value = false;
};

const handleUserLoginCancel = () => {
  userLoginModalVisible.value = false;
};

const handleLogout = () => {
  userStore.logout();
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

const handleLanguageChange = (value: Locale) => {
  setLocale(value);
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

.language-selector {
  width: 120px;
}

.language-selector :deep(.ant-select-selector) {
  border-radius: 6px;
}

.user-login-btn {
  background: #1890ff;
  border-color: #1890ff;
}

.user-login-btn:hover {
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

.user-info {
  display: flex;
  align-items: center;
}

.user-info-btn {
  color: #1890ff;
  font-weight: 500;
  border: none;
  box-shadow: none;
}

.user-info-btn:hover {
  color: #40a9ff;
  background: rgba(24, 144, 255, 0.1);
}

.login-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
