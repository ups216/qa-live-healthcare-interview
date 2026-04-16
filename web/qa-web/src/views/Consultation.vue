<template>
  <div class="consultation">
    <div class="consultation-container">
      <div v-if="!currentPatient" class="auth-section">
        <div class="auth-card">
          <h1>患者登录</h1>
          <p>使用用户名和密码登录，首次使用可直接注册。</p>
          <a-form :model="authForm" @finish="handlePatientAuth" layout="vertical">
            <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
              <a-input v-model:value="authForm.username" size="large" placeholder="请输入用户名" />
            </a-form-item>
            <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
              <a-input-password v-model:value="authForm.password" size="large" placeholder="请输入密码" />
            </a-form-item>
            <a-form-item v-if="authMode === 'register'" label="显示名称" name="displayName">
              <a-input v-model:value="authForm.displayName" size="large" placeholder="请输入显示名称，可选" />
            </a-form-item>
            <a-form-item>
              <a-space style="width: 100%" direction="vertical" :size="12">
                <a-button type="primary" html-type="submit" size="large" block>
                  {{ authMode === 'login' ? '登录' : '注册并登录' }}
                </a-button>
                <a-button size="large" block @click="toggleAuthMode">
                  {{ authMode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <div v-else class="patient-portal">
        <div class="portal-header">
          <div class="patient-info">
            <UserOutlined class="patient-icon-large" />
            <div>
              <h1>{{ currentPatient.displayName }} 的问诊中心</h1>
              <p>@{{ currentPatient.username }}</p>
            </div>
          </div>
          <div class="portal-actions">
            <a-button @click="logoutPatient">
              <LogoutOutlined />
              切换用户
            </a-button>
          </div>
        </div>

        <div v-if="selectedDoctor" class="selected-doctor">
          <a-alert
            :message="`当前问诊医生：${selectedDoctor.name} - ${selectedDoctor.department}`"
            type="success"
            show-icon
            closable
            @close="clearSelectedDoctor"
          />
        </div>

        <div class="questions-section">
          <div class="section-header">
            <h2>我的问题</h2>
            <a-button type="primary" @click="showSubmitModal">
              <PlusOutlined />
              提交问题
            </a-button>
          </div>

          <a-empty v-if="myQuestions.length === 0" description="暂时还没有提交过问题" />

          <div v-else class="my-questions-list">
            <a-card v-for="question in myQuestions" :key="question.id" class="question-item">
              <template #title>
                <div class="question-title">
                  <span>{{ question.doctorName }}</span>
                  <a-tag :color="question.status === 'answered' ? 'green' : 'orange'">
                    {{ question.status === 'answered' ? '已回复' : '待回复' }}
                  </a-tag>
                </div>
              </template>

              <div class="question-detail">
                <p class="question-text"><strong>问题：</strong>{{ question.question }}</p>
                <p class="submit-time">提交时间：{{ formatTime(question.submitTime) }}</p>

                <div v-if="question.status === 'answered'" class="answer-section">
                  <a-divider />
                  <p class="answer-text"><strong>医生回复：</strong>{{ question.answer }}</p>
                  <p class="answer-time">回复时间：{{ formatTime(question.answerTime!) }}</p>
                </div>
              </div>
            </a-card>
          </div>
        </div>
      </div>
    </div>

    <a-modal
      v-model:open="submitModalVisible"
      title="提交问题"
      @ok="submitQuestion"
      @cancel="closeSubmitModal"
      :confirmLoading="submitting"
      width="640px"
      centered
    >
      <div class="submit-modal-body">
        <a-form layout="vertical">
          <a-form-item label="选择医生" required>
            <a-select
              v-model:value="questionForm.doctorId"
              size="large"
              placeholder="请选择医生"
              :disabled="!!selectedDoctor"
              popupClassName="doctor-select-dropdown"
            >
              <a-select-option v-for="doctor in availableDoctors" :key="doctor.id" :value="doctor.id">
                <div class="doctor-option">
                  <img :src="doctor.avatar" :alt="doctor.name" class="doctor-option-avatar" />
                  <div class="doctor-option-content">
                    <div class="doctor-option-name">{{ doctor.name }}</div>
                    <div class="doctor-option-meta">{{ doctor.title }} / {{ doctor.department }}</div>
                  </div>
                </div>
              </a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="问题描述" required>
            <a-textarea
              v-model:value="questionForm.question"
              :rows="6"
              :maxlength="500"
              show-count
              placeholder="请详细描述症状、持续时间、是否已自行用药等信息"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import { LogoutOutlined, PlusOutlined, UserOutlined } from '@ant-design/icons-vue';
import { store, type Doctor } from '../store';

const route = useRoute();
const currentPatient = computed(() => store.state.currentPatient);
const myQuestions = computed(() => currentPatient.value ? store.getQuestionsByPatient(currentPatient.value.id) : []);

const authMode = ref<'login' | 'register'>('login');
const authForm = reactive({ username: '', password: '', displayName: '' });

const selectedDoctor = ref<Doctor | null>(null);
const submitModalVisible = ref(false);
const submitting = ref(false);
const questionForm = reactive({ doctorId: '', question: '' });
const availableDoctors = computed(() => selectedDoctor.value ? [selectedDoctor.value] : store.getActiveDoctors());

onMounted(async () => {
  try {
    await store.loadDoctors();
    if (currentPatient.value) {
      await store.loadQuestions({ patientId: currentPatient.value.id });
    }
  } catch {
    message.error('医生数据加载失败');
  }

  const doctorUsername = route.params.doctorUsername as string;
  if (doctorUsername) {
    const doctor = store.getDoctorByUsername(doctorUsername);
    if (doctor && doctor.isActive) {
      selectedDoctor.value = doctor;
      questionForm.doctorId = doctor.id;
    }
  }
});

const toggleAuthMode = () => {
  authMode.value = authMode.value === 'login' ? 'register' : 'login';
};

const handlePatientAuth = async () => {
  try {
    if (authMode.value === 'login') {
      await store.loginPatient(authForm.username, authForm.password);
      message.success('登录成功');
    } else {
      await store.registerPatient(authForm.username, authForm.password, authForm.displayName);
      message.success('注册成功');
    }
  } catch (error: any) {
    message.error(error.message || '操作失败');
  }
};

const logoutPatient = () => {
  store.logoutPatient();
  selectedDoctor.value = null;
  message.success('已切换用户');
};

const clearSelectedDoctor = () => {
  selectedDoctor.value = null;
  questionForm.doctorId = '';
};

const showSubmitModal = () => {
  if (selectedDoctor.value) {
    questionForm.doctorId = selectedDoctor.value.id;
  }
  submitModalVisible.value = true;
};

const closeSubmitModal = () => {
  submitModalVisible.value = false;
  if (!selectedDoctor.value) {
    questionForm.doctorId = '';
  }
  questionForm.question = '';
};

const submitQuestion = async () => {
  if (!questionForm.doctorId) {
    return message.error('请选择医生');
  }

  if (!questionForm.question.trim()) {
    return message.error('请输入问题描述');
  }

  const doctor = store.state.doctors.find((item) => item.id === questionForm.doctorId);
  if (!doctor || !currentPatient.value) {
    return;
  }

  submitting.value = true;
  try {
    await store.addQuestion({
      patientId: currentPatient.value.id,
      patientName: currentPatient.value.displayName,
      doctorId: doctor.id,
      doctorName: doctor.name,
      question: questionForm.question.trim(),
    });
    message.success('问题提交成功');
    closeSubmitModal();
  } catch {
    message.error('问题提交失败');
  } finally {
    submitting.value = false;
  }
};

const formatTime = (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm');
</script>

<style scoped>
.consultation { min-height: calc(100vh - 64px); padding-top: 64px; background: #f0f2f5; }
.consultation-container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.auth-section { min-height: calc(100vh - 112px); display: flex; align-items: center; justify-content: center; }
.auth-card { background: #fff; border-radius: 16px; padding: 48px; width: 100%; max-width: 450px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); }
.auth-card h1 { font-size: 28px; font-weight: 700; color: #333; text-align: center; margin-bottom: 8px; }
.auth-card > p { font-size: 16px; color: #666; text-align: center; margin-bottom: 32px; }
.patient-portal { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); }
.portal-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 32px; display: flex; align-items: center; justify-content: space-between; color: #fff; }
.patient-info { display: flex; align-items: center; gap: 16px; }
.patient-icon-large { font-size: 48px; color: #fff; }
.patient-info h1 { font-size: 24px; font-weight: 600; color: #fff; margin: 0 0 4px; }
.patient-info p { font-size: 14px; color: rgba(255, 255, 255, 0.85); margin: 0; }
.selected-doctor { padding: 16px 24px; background: #f6ffed; border-bottom: 1px solid #e8e8e8; }
.questions-section { padding: 24px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; gap: 16px; }
.my-questions-list { display: flex; flex-direction: column; gap: 16px; }
.question-title { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.submit-modal-body { padding-top: 8px; }
.doctor-option { display: flex; align-items: center; gap: 12px; min-height: 44px; }
.doctor-option-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.doctor-option-content { min-width: 0; }
.doctor-option-name { font-weight: 600; color: #333; }
.doctor-option-meta { font-size: 12px; color: #999; }

:deep(.ant-select-single.ant-select-lg .ant-select-selector) {
  min-height: 56px;
  align-items: center;
  padding-top: 6px;
  padding-bottom: 6px;
}

:deep(.ant-select-single.ant-select-lg .ant-select-selection-item) {
  line-height: 1.2 !important;
  display: flex;
  align-items: center;
  min-height: 44px;
  padding-inline-end: 24px;
}

:deep(.ant-select-single.ant-select-lg .ant-select-selection-item .doctor-option) {
  width: 100%;
}

:deep(.ant-select-single.ant-select-lg .ant-select-selection-item .doctor-option-avatar) {
  width: 32px;
  height: 32px;
}

@media (max-width: 768px) {
  .consultation-container { padding: 16px; }
  .auth-card { padding: 32px 24px; }
  .portal-header { padding: 24px 20px; flex-direction: column; align-items: flex-start; gap: 16px; }
  .section-header { flex-direction: column; align-items: stretch; }
}
</style>
