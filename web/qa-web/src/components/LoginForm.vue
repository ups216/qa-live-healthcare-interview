<template>
  <a-form
    class="login-form"
    layout="vertical"
    :model="formState"
    :rules="rules"
    @finish="handleSubmit"
  >
    <a-form-item label="用户名" name="username">
      <a-input
        v-model:value="formState.username"
        size="large"
        placeholder="请输入用户名"
        autocomplete="username"
      >
        <template #prefix>
          <UserOutlined />
        </template>
      </a-input>
    </a-form-item>

    <a-form-item label="密码" name="password">
      <a-input-password
        v-model:value="formState.password"
        size="large"
        placeholder="请输入密码"
        autocomplete="current-password"
      >
        <template #prefix>
          <LockOutlined />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item>
      <a-button type="primary" html-type="submit" size="large" block :loading="loading">
        登录
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { store } from '../store';

const emit = defineEmits<{ (e: 'success'): void }>();

const loading = ref(false);

const formState = reactive({
  username: '',
  password: '',
});

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
};

async function handleSubmit() {
  loading.value = true;
  try {
    await store.loginPatient(formState.username, formState.password);
    message.success('登录成功');
    emit('success');
  } catch (e) {
    // 后端对"用户不存在"与"密码错误"返回相同的对外提示（防用户名枚举），
    // 这里原样透出即可，不做二次区分。
    message.error(e instanceof Error ? e.message : '登录失败，请重试');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-form {
  margin-top: 16px;
}
</style>
