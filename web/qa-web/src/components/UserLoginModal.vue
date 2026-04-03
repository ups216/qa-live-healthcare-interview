<template>
  <a-modal
    :visible="visible"
    title="用户登录"
    :footer="null"
    width="400px"
    @cancel="handleCancel"
    @update:visible="(value) => emit('update:visible', value)"
  >
    <div class="user-login-modal">
      <a-form
        :model="formState"
        :rules="rules"
        @finish="onFinish"
        layout="vertical"
        class="login-form"
      >
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="formState.username"
            size="large"
            placeholder="请输入用户名"
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

        <div class="register-link">
          <span>还没有账号？</span>
          <a @click="showRegister = true">立即注册</a>
        </div>
      </a-form>

      <a-modal
        :visible="showRegister"
        title="用户注册"
        :footer="null"
        width="400px"
        @update:visible="(value) => showRegister = value"
      >
        <a-form
          :model="registerFormState"
          :rules="registerRules"
          @finish="onRegisterFinish"
          layout="vertical"
          class="register-form"
        >
          <a-form-item label="用户名" name="username">
            <a-input
              v-model:value="registerFormState.username"
              size="large"
              placeholder="请输入用户名"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="registerFormState.password"
              size="large"
              placeholder="请输入密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item label="确认密码" name="confirmPassword">
            <a-input-password
              v-model:value="registerFormState.confirmPassword"
              size="large"
              placeholder="请再次输入密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item label="姓名" name="name">
            <a-input
              v-model:value="registerFormState.name"
              size="large"
              placeholder="请输入真实姓名"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="邮箱" name="email">
            <a-input
              v-model:value="registerFormState.email"
              size="large"
              placeholder="请输入邮箱地址"
            >
              <template #prefix>
                <MailOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item label="手机号" name="phone">
            <a-input
              v-model:value="registerFormState.phone"
              size="large"
              placeholder="请输入手机号"
            >
              <template #prefix>
                <PhoneOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block :loading="registerLoading">
              注册
            </a-button>
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons-vue';
import { userStore } from '../store/userStore';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
  success: [];
  cancel: [];
}>();

const loading = ref(false);
const registerLoading = ref(false);
const showRegister = ref(false);

const formState = reactive({
  username: '',
  password: '',
});

const registerFormState = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: '',
});

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
};

const registerRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    {
      validator: (rule: any, value: string) => {
        if (value !== registerFormState.password) {
          return Promise.reject('两次输入的密码不一致');
        }
        return Promise.resolve();
      },
    },
  ],
  name: [{ required: true, message: '请输入姓名' }],
  email: [{ type: 'email', message: '请输入有效的邮箱地址' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }],
};

const handleCancel = () => {
  emit('update:visible', false);
  emit('cancel');
};

const onFinish = async () => {
  loading.value = true;

  try {
    const success = await userStore.login(formState.username, formState.password);

    if (success) {
      message.success('登录成功');
      emit('update:visible', false);
      emit('success');
    } else {
      message.error(userStore.errorMessage || '用户名或密码错误');
    }
  } catch (error) {
    message.error('登录失败，请检查网络连接');
    console.error('Login error:', error);
  } finally {
    loading.value = false;
  }
};

const onRegisterFinish = async () => {
  registerLoading.value = true;

  try {
    const success = await userStore.register(
      registerFormState.username,
      registerFormState.password,
      registerFormState.name,
      registerFormState.email,
      registerFormState.phone
    );

    if (success) {
      message.success('注册成功');
      showRegister.value = false;
      emit('update:visible', false);
      emit('success');
    } else {
      message.error(userStore.errorMessage || '注册失败');
    }
  } catch (error) {
    message.error('注册失败，请检查网络连接');
    console.error('Register error:', error);
  } finally {
    registerLoading.value = false;
  }
};
</script>

<style scoped>
.login-form {
  margin-bottom: 0;
}

.register-link {
  text-align: center;
  margin-top: 16px;
}

.register-link a {
  color: #1890ff;
  cursor: pointer;
}

.register-link a:hover {
  color: #40a9ff;
}
</style>