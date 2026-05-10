<template>
  <div class="login-form-container">
    <a-form
      :model="formState"
      @finish="handleSubmit"
      @finishFailed="handleFinishFailed"
      layout="vertical"
    >
      <a-form-item
        label="用户名"
        name="username"
        :rules="[
          { required: true, message: '请输入用户名' },
          { min: 3, max: 50, message: '用户名长度必须在3-50之间' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线' }
        ]"
      >
        <a-input
          v-model:value="formState.username"
          placeholder="请输入用户名"
          size="large"
        >
          <template #prefix>
            <UserOutlined />
          </template>
        </a-input>
      </a-form-item>

      <a-form-item
        label="密码"
        name="password"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, max: 128, message: '密码长度必须在8-128之间' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '密码必须包含字母和数字' }
        ]"
      >
        <a-input-password
          v-model:value="formState.password"
          placeholder="请输入密码"
          size="large"
        >
          <template #prefix>
            <LockOutlined />
          </template>
        </a-input-password>
      </a-form-item>

      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          size="large"
          :loading="loading"
          block
        >
          登录
        </a-button>
      </a-form-item>

      <div v-if="error" class="error-message">
        <a-alert :message="error" type="error" show-icon closable @close="clearError" />
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';

interface LoginCredentials {
  username: string;
  password: string;
}

interface Props {
  loading?: boolean;
  error?: string;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: '',
});

const emit = defineEmits<{
  (e: 'submit', credentials: LoginCredentials): void;
}>();

const formState = reactive({
  username: '',
  password: '',
});

const handleSubmit = (values: any) => {
  emit('submit', {
    username: values.username,
    password: values.password,
  });
};

const handleFinishFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo);
};

const clearError = () => {
  // 清除错误信息的逻辑由父组件处理
};
</script>

<style scoped>
.login-form-container {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.error-message {
  margin-top: 16px;
}
</style>
