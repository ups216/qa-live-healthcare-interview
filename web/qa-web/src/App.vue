<script setup lang="ts">
import { onMounted } from 'vue';
import { RouterView } from 'vue-router';
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import { store } from './store';

onMounted(async () => {
  // 需求3：先依据本地 token 恢复问诊用户登录态（覆盖"刷新浏览器"场景）。
  // 必须在路由守卫之前完成：守卫依赖 currentPatient 判断是否需要重定向，
  // 若恢复得晚，首屏访问 /consultation 会被误判为未登录而跳走。
  try {
    await store.restorePatient();
  } catch {
    // token 无效时 restorePatient 返回 false 而不抛错，此处兜底避免阻塞后续加载
  }

  // 需求2：医生数据改由后端提供。在根组件挂载时统一预加载，
  // 使 Home / Doctors / Consultation / DoctorRoom 等视图都能通过同一份 state.doctors 取数。
  // 加载失败不阻塞页面渲染，各视图会依据 state.doctorsError 展示错误态与重试入口。
  try {
    await store.loadDoctors();
  } catch {
    // 错误已写入 state.doctorsError，此处静默避免 Unhandled Promise Rejection
  }
});
</script>

<template>
  <a-layout class="app-layout">
    <AppHeader />
    <a-layout-content class="app-content">
      <RouterView />
    </a-layout-content>
    <AppFooter />
  </a-layout>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.app-layout {
  min-height: 100vh;
}

.app-content {
  min-height: calc(100vh - 64px);
}
</style>
