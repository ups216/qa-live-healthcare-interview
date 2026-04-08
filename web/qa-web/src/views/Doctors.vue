<template>
  <div class="doctors-page">
    <div class="page-header">
      <h1>医生团队</h1>
      <p>我们的专业医疗团队随时为您服务</p>
    </div>

    <div class="doctors-container">
      <!-- 加载中：明确反馈，不白屏 -->
      <div v-if="store.state.doctorsLoading" class="state-message">
        <a-spin size="large" />
        <p>加载中...</p>
      </div>

      <!-- 请求失败：可识别错误提示 -->
      <div v-else-if="store.state.doctorsError" class="state-message error">
        <a-alert type="error" :message="store.state.doctorsError" show-icon />
        <a-button type="primary" @click="store.fetchDoctors()">重试</a-button>
      </div>

      <!-- 空列表：暂无医生 -->
      <div v-else-if="!store.state.doctors.length" class="state-message">
        <a-empty description="暂无医生" />
      </div>

      <!-- 正常列表 -->
      <div v-else class="doctors-grid">
        <a-card
          v-for="doctor in store.state.doctors"
          :key="doctor.id"
          class="doctor-card"
          :class="{ active: doctor.isActive }"
        >
          <div class="card-header">
            <img :src="doctor.avatar" :alt="doctor.name" class="doctor-avatar" />
            <a-badge
              :status="doctor.isActive ? 'processing' : 'default'"
              :text="doctor.isActive ? '在线' : '离线'"
            />
          </div>
          <div class="card-body">
            <h3>{{ doctor.name }}</h3>
            <p class="doctor-title">{{ doctor.title }}</p>
            <p class="doctor-department">{{ doctor.department }}</p>
            <p class="doctor-experience">{{ doctor.experience }}</p>
            <div class="doctor-specialties">
              <a-tag v-for="specialty in doctor.specialties" :key="specialty" color="blue">
                {{ specialty }}
              </a-tag>
            </div>
          </div>
          <div class="card-footer">
            <a-button
              type="primary"
              block
              :disabled="!doctor.isActive"
              @click="goToConsultation(doctor)"
            >
              {{ doctor.isActive ? '进入诊室' : '暂未开放' }}
            </a-button>
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { store, Doctor } from '../store';

const router = useRouter();

const goToConsultation = (doctor: Doctor) => {
  router.push(`/consultation/${doctor.username}`);
};
</script>

<style scoped>
.doctors-page {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: #f0f2f5;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80px 24px;
  text-align: center;
  color: #fff;
}

.page-header h1 {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 16px;
}

.page-header p {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.doctors-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 24px;
}

.state-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 280px;
  color: #666;
}

.state-message.error {
  gap: 24px;
}

.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.doctor-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.doctor-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.doctor-card.active {
  border: 2px solid #52c41a;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.doctor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.card-body h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.doctor-title {
  font-size: 16px;
  color: #1890ff;
  font-weight: 500;
  margin-bottom: 4px;
}

.doctor-department {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.doctor-experience {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.doctor-specialties {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 32px;
  }

  .page-header p {
    font-size: 16px;
  }

  .doctors-grid {
    grid-template-columns: 1fr;
  }
}
</style>
