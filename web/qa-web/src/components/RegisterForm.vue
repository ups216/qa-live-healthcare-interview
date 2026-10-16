<template>
  <a-form
    class="register-form"
    layout="vertical"
    :model="formState"
    :rules="rules"
    @finish="handleSubmit"
  >
    <a-form-item label="用户名" name="username">
      <a-input v-model:value="formState.username" size="large" placeholder="3~32 位，注册后不可修改">
        <template #prefix>
          <UserOutlined />
        </template>
      </a-input>
    </a-form-item>

    <a-form-item label="密码" name="password">
      <a-input-password
        v-model:value="formState.password"
        size="large"
        placeholder="至少 6 位"
        autocomplete="new-password"
      >
        <template #prefix>
          <LockOutlined />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item label="确认密码" name="confirmPassword">
      <a-input-password
        v-model:value="formState.confirmPassword"
        size="large"
        placeholder="请再次输入密码"
        autocomplete="new-password"
      >
        <template #prefix>
          <LockOutlined />
        </template>
      </a-input-password>
    </a-form-item>

    <a-form-item label="姓名" name="name">
      <a-input v-model:value="formState.name" size="large" placeholder="请输入您的姓名">
        <template #prefix>
          <IdcardOutlined />
        </template>
      </a-input>
    </a-form-item>

    <a-form-item label="性别" name="gender">
      <a-radio-group v-model:value="formState.gender">
        <a-radio value="男">男</a-radio>
        <a-radio value="女">女</a-radio>
      </a-radio-group>
    </a-form-item>

    <a-form-item label="生日" name="birthday">
      <a-date-picker
        v-model:value="formState.birthday"
        style="width: 100%"
        placeholder="选填"
        :value-format="'YYYY-MM-DD'"
      />
    </a-form-item>

    <a-form-item>
      <a-button type="primary" html-type="submit" size="large" block :loading="loading">
        注册
      </a-button>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, IdcardOutlined } from '@ant-design/icons-vue';
import type { Dayjs } from 'dayjs';
import { store } from '../store';

const emit = defineEmits<{ (e: 'success'): void }>();

const loading = ref(false);

const formState = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '' as string | undefined,
  birthday: null as Dayjs | string | null,
});

// 确认密码需要与密码比对，因此写成自定义校验规则而非简单的 required
const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 32, message: '用户名需为 3~32 位' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 64, message: '密码至少需要 6 位' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码' },
    {
      validator: async (_rule: unknown, value: string) => {
        if (value && value !== formState.password) {
          throw new Error('两次输入的密码不一致');
        }
      },
    },
  ],
  name: [{ required: true, message: '请输入姓名' }],
};

async function handleSubmit() {
  loading.value = true;
  try {
    await store.registerPatient({
      username: formState.username,
      password: formState.password,
      name: formState.name,
      gender: formState.gender,
      // a-date-picker 配合 value-format 后取出的是字符串
      birthday: typeof formState.birthday === 'string' ? formState.birthday : undefined,
    });

    // 注册成功后自动登录一次，省去用户再输一遍密码
    await store.loginPatient(formState.username, formState.password);
    emit('success');
  } catch (e) {
    message.error(e instanceof Error ? e.message : '注册失败，请重试');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-form {
  margin-top: 16px;
}
</style>
