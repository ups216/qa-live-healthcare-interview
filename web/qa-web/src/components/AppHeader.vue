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
          首页
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          问诊
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          医生
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          关于
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <!-- 需求3：已登录时显示患者名 + 退出；未登录时显示登录入口 -->
        <div v-if="currentPatient" class="patient-entry">
          <span class="patient-name">{{ currentPatient.name }}</span>
          <a-button size="small" @click="handleLogout">退出</a-button>
        </div>
        <a-button v-else size="small" class="patient-btn" @click="navigateTo('/patient/login')">
          用户登录
        </a-button>

        <a-button type="primary" class="login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          医生登录
        </a-button>
        <!-- 需求1：语言切换下拉，置于页面最右上角 -->
        <LangSwitcher />
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined } from '@ant-design/icons-vue';
import LangSwitcher from './LangSwitcher.vue';
import { store } from '../store';

const router = useRouter();
const route = useRoute();
const selectedKeys = ref<string[]>(['home']);

// 需求3：头部展示当前登录的问诊用户
const currentPatient = computed(() => store.state.currentPatient);

function handleLogout() {
  store.logoutPatient();
  message.success('已退出登录');
  // 退出后若在需要登录的页面，回到首页，避免停留在无权限视图
  if (route.path.startsWith('/consultation')) {
    router.push('/');
  }
}

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

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
  flex: none;
}

/* 包裹"医生登录"与语言切换器，统一控制右侧间距 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: none;
}

.patient-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: none;
}

.patient-name {
  font-size: 14px;
  color: #333;
  max-width: 96px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.patient-btn {
  flex: none;
}

/* 窄屏优先保住语言切换器与医生登录，隐去患者名只留退出按钮 */
@media (max-width: 900px) {
  .patient-name {
    display: none;
  }
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}
</style>
