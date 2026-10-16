<template>
  <div class="doctors-page">
    <div class="page-header">
      <h1>医生团队</h1>
      <p>我们的专业医疗团队随时为您服务</p>
    </div>

    <div class="doctors-container">
      <div class="doctors-grid">
        <!-- 加载中：骨架卡，避免白屏与"暂无医生"的误读 -->
        <template v-if="loading">
          <a-card v-for="n in 4" :key="`skeleton-${n}`" class="doctor-card">
            <a-skeleton active :paragraph="{ rows: 4 }" avatar />
          </a-card>
        </template>

        <!-- 加载失败：给出原因与重试入口 -->
        <div v-else-if="error" class="state-wrap">
          <a-result
            status="error"
            title="加载医生列表失败"
            :sub-title="error"
          >
            <template #extra>
              <a-button type="primary" :loading="retrying" @click="handleRetry">
                重试
              </a-button>
            </template>
          </a-result>
        </div>

        <!-- 成功但为空：区分"确实没有医生"与"还没加载完" -->
        <div v-else-if="!allDoctors.length" class="state-wrap">
          <a-empty description="暂无医生信息" />
        </div>

        <!-- 正常渲染：包一层 template，避免在同一元素上同时使用 v-for 与 v-else -->
        <template v-else>
          <a-card
            v-for="doctor in allDoctors"
            :key="doctor.id"
            class="doctor-card"
            :class="{ 'active': doctor.isActive }"
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
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { store, Doctor } from '../store';

const router = useRouter();

// 需求2：医生数据来自后端 MySQL（经 store.loadDoctors 统一加载），
// 这里不再直接持有数据，只消费 store 的状态，并据此呈现 loading / error / empty / success 四态。
const allDoctors = computed(() => store.state.doctors);
const loading = computed(() => store.state.doctorsLoading);
const error = computed(() => store.state.doctorsError);

const retrying = ref(false);

async function handleRetry() {
  retrying.value = true;
  try {
    await store.loadDoctors();
  } catch {
    // 失败信息仍在 store.state.doctorsError 中，页面保持错误态
  } finally {
    retrying.value = false;
  }
}

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

.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

/* loading / error / empty 三态时，内容不需要分栅格，独占整行 */
.state-wrap {
  grid-column: 1 / -1;
  padding: 48px 0;
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
