<template>
  <div class="patient-login">
    <div class="login-container">
      <div class="login-header">
        <h1>{{ isRegister ? '注册问诊账号' : '用户登录' }}</h1>
        <p>{{ isRegister ? '创建账号后即可向医生提问' : '登录后即可使用在线问诊服务' }}</p>
      </div>

      <a-tabs v-model:activeKey="activeKey" centered>
        <a-tab-pane key="login" tab="登录">
          <LoginForm @success="handleSuccess" />
        </a-tab-pane>
        <a-tab-pane key="register" tab="注册">
          <RegisterForm @success="handleSuccess" />
        </a-tab-pane>
      </a-tabs>

      <div class="login-tips">
        <a-alert
          message="演示说明"
          description="注册后账号会存入 MySQL 数据库（t_patient 表），密码使用 BCrypt 加密存储。"
          type="info"
          show-icon
          closable
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message as Message } from 'ant-design-vue';
import LoginForm from '../components/LoginForm.vue';
import RegisterForm from '../components/RegisterForm.vue';

const route = useRoute();
const router = useRouter();

const activeKey = ref<string>('login');
const isRegister = computed(() => activeKey.value === 'register');

/**
 * 登录/注册成功后跳转；优先回到路由守卫暂存的 redirect 目标，
 * 没有则回到问诊首页，符合"登录后回到刚才想去的页面"的预期。
 */
function handleSuccess() {
  const redirect = route.query.redirect as string | undefined;
  // 注册成功后给一句明确反馈，登录成功则由目标页面呈现，避免重复提示
  if (isRegister.value) {
    Message.success('注册成功，已自动登录');
  }
  router.push(redirect || '/consultation');
}
</script>

<style scoped>
.patient-login {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  background: #fff;
  border-radius: 16px;
  padding: 40px 48px 32px;
  width: 100%;
  max-width: 460px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  margin: 24px;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-tips {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .login-container {
    padding: 32px 24px 24px;
  }

  .login-header h1 {
    font-size: 24px;
  }
}
</style>
