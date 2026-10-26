<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <h1>专业在线医疗问诊平台</h1>
        <p class="hero-subtitle">连接专业医生与患者,提供便捷、高效的医疗咨询服务</p>
        <div class="hero-features">
          <div class="feature-item">
            <CheckCircleOutlined class="feature-icon" />
            <span>专业医生团队</span>
          </div>
          <div class="feature-item">
            <CheckCircleOutlined class="feature-icon" />
            <span>实时在线问诊</span>
          </div>
          <div class="feature-item">
            <CheckCircleOutlined class="feature-icon" />
            <span>隐私安全保护</span>
          </div>
        </div>
        <div class="hero-actions">
          <a-button type="primary" size="large" @click="navigateTo('/consultation')">
            立即问诊
          </a-button>
          <a-button size="large" @click="navigateTo('/doctors')">
            查看医生
          </a-button>
        </div>
      </div>
      <div class="hero-image">
        <img src="https://images.pexels.com/photos/4386467/pexels-photo-4386467.jpeg?auto=compress&cs=tinysrgb&w=600" alt="Healthcare" />
      </div>
    </section>

    <section class="statistics">
      <div class="stat-card">
        <div class="stat-icon doctor">
          <TeamOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.totalDoctors }}</h3>
          <p>专业医生</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon question">
          <FileTextOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.totalQuestions }}</h3>
          <p>问题总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active">
          <ClockCircleOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.activeSessions }}</h3>
          <p>待响应问题</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon session">
          <UserOutlined />
        </div>
        <div class="stat-info">
          <h3>{{ statistics.totalSessions }}</h3>
          <p>在线诊室</p>
        </div>
      </div>
    </section>

    <section class="active-rooms">
      <h2>开放诊室</h2>
      <p class="section-subtitle">以下医生诊室正在开放,欢迎咨询</p>
      <div class="rooms-grid">
        <div
          v-for="doctor in activeDoctors"
          :key="doctor.id"
          class="room-card"
          @click="navigateTo(`/consultation/${doctor.username}`)"
        >
          <div class="room-header">
            <img :src="doctor.avatar" :alt="doctor.name" class="doctor-avatar" />
            <a-badge status="processing" text="在线" />
          </div>
          <div class="room-body">
            <h3>{{ doctor.name }}</h3>
            <p class="doctor-title">{{ doctor.title }} · {{ doctor.department }}</p>
            <p class="doctor-experience">{{ doctor.experience }}</p>
            <div class="doctor-specialties">
              <a-tag v-for="specialty in doctor.specialties" :key="specialty" color="blue">
                {{ specialty }}
              </a-tag>
            </div>
          </div>
          <div class="room-footer">
            <a-button type="primary" block>进入诊室</a-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { store } from '../store';
import {
  CheckCircleOutlined,
  TeamOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  UserOutlined
} from '@ant-design/icons-vue';

const router = useRouter();

const statistics = computed(() => store.getStatistics());
const activeDoctors = computed(() => store.getActiveDoctors());

const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<style scoped>
.home {
  padding-top: 64px;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 24px;
  gap: 60px;
}

.hero-content {
  flex: 1;
}

.hero h1 {
  font-size: 48px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 24px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 20px;
  color: #666;
  margin-bottom: 40px;
  line-height: 1.6;
}

.hero-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  color: #333;
}

.feature-icon {
  font-size: 24px;
  color: #52c41a;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.hero-image {
  flex: 1;
  max-width: 500px;
}

.hero-image img {
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.statistics {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.doctor {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.question {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.session {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.stat-info p {
  font-size: 16px;
  color: #666;
  margin: 4px 0 0;
}

.active-rooms {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 24px;
}

.active-rooms h2 {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 18px;
  color: #666;
  text-align: center;
  margin-bottom: 48px;
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.room-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.room-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.room-header {
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.doctor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
}

.room-body {
  padding: 24px;
}

.room-body h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.doctor-title {
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
}

.room-footer {
  padding: 0 24px 24px;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    padding: 40px 24px;
  }

  .hero h1 {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .statistics {
    grid-template-columns: 1fr;
  }

  .active-rooms h2 {
    font-size: 28px;
  }

  .rooms-grid {
    grid-template-columns: 1fr;
  }
}
</style>
