<template>
  <div class="patient-room">
    <div class="patient-header">
      <h1>{{ t('patientRoom.title') }}</h1>
      <div class="patient-info">
        <span class="welcome">{{ t('patientRoom.welcome') }}, {{ store.state.currentPatient?.name || '' }}</span>
        <a-button type="text" @click="logout">
          <LogoutOutlined />
          {{ t('patientRoom.logout') }}
        </a-button>
      </div>
    </div>

    <div class="patient-content">
      <div class="section">
        <h2>{{ t('patientRoom.doctors') }}</h2>
        <a-card-grid style="width: 25%;" v-for="doctor in doctors" :key="doctor.id">
          <div class="doctor-card">
            <div class="doctor-avatar">
              <img :src="doctor.avatar" :alt="doctor.name" />
            </div>
            <h3>{{ doctor.name }}</h3>
            <p>{{ doctor.title }}</p>
            <p>{{ doctor.department }}</p>
            <a-button type="primary" size="small" @click="startConsultation(doctor)">
              {{ t('patientRoom.consult') }}
            </a-button>
          </div>
        </a-card-grid>
      </div>

      <div class="section">
        <h2>{{ t('patientRoom.questions') }}</h2>
        <a-list
          :data-source="questions"
          item-layout="horizontal"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta
                :description="item.question"
              >
                <template #title>
                  <a href="#">问题 #{{ item.id }}</a>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import { LogoutOutlined } from '@ant-design/icons-vue';
import { store } from '../store';
import { t } from '../locales';

const router = useRouter();
const route = useRoute();

// 使用computed来获取store中的数据，这样数据会自动更新
const doctors = computed(() => store.state.doctors);
const questions = computed(() => store.state.questions);

onMounted(async () => {
  if (!store.state.currentPatient) {
    message.error(t('patientRoom.loginFirst'));
    router.push('/patient/login');
    return;
  }

  // 重新加载所有数据，确保医生和问题列表是最新的
  await store.reload();
});

const startConsultation = (doctor: any) => {
  router.push(`/consultation?doctorId=${doctor.id}&doctorName=${doctor.name}`);
};

const logout = () => {
  store.logoutPatient();
  message.success(t('patientRoom.logoutSuccess'));
  router.push('/patient/login');
};
</script>

<style scoped>
.patient-room {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 80px 20px 20px;
}

.patient-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome {
  font-size: 16px;
  color: #333;
}

.patient-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section {
  margin-bottom: 30px;
}

.section h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.doctor-card {
  text-align: center;
  padding: 16px;
}

.doctor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 12px;
}

.doctor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doctor-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
}

.doctor-card p {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px;
}

.doctor-card .ant-btn {
  margin-top: 8px;
  background: #52c41a;
  border-color: #52c41a;
}

.doctor-card .ant-btn:hover {
  background: #73d13d !important;
  border-color: #73d13d !important;
}
</style>
