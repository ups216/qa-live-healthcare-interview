<template>
  <div class="doctors-page">
    <div class="page-header">
      <h1>{{ $t('home.activeRooms.title') }}</h1>
      <p>{{ $t('home.activeRooms.subtitle') }}</p>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-container">
        <a-input
          v-model:value="filters.name"
          placeholder="输入医生姓名"
          class="filter-item"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <UserOutlined />
          </template>
        </a-input>
        
        <a-select
          v-model:value="filters.department"
          placeholder="选择科室"
          class="filter-item"
          @change="handleSearch"
        >
          <a-option value="">全部科室</a-option>
          <a-option v-for="dept in departments" :key="dept" :value="dept">
            {{ dept }}
          </a-option>
        </a-select>

        <a-select
          v-model:value="filters.title"
          placeholder="选择职称"
          class="filter-item"
          @change="handleSearch"
        >
          <a-option value="">全部职称</a-option>
          <a-option v-for="title in titles" :key="title" :value="title">
            {{ title }}
          </a-option>
        </a-select>

        <a-select
          v-model:value="filters.isActive"
          placeholder="选择在线状态"
          class="filter-item"
          @change="handleSearch"
        >
          <a-option value="">全部状态</a-option>
          <a-option value="true">在线</a-option>
          <a-option value="false">离线</a-option>
        </a-select>

        <a-button type="primary" class="filter-item" @click="handleSearch">
          <SearchOutlined />
          搜索
        </a-button>

        <a-button class="filter-item" @click="resetFilters">
          重置
        </a-button>
      </div>
    </div>

    <!-- 医生列表 -->
    <div class="doctors-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <a-spin size="large" tip="加载中..." />
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <a-alert
          type="error"
          message="加载失败"
          description="无法加载医生数据，请稍后重试"
          show-icon
        />
        <a-button type="primary" style="margin-top: 16px" @click="loadDoctors">
          重新加载
        </a-button>
      </div>

      <!-- 空状态 -->
      <div v-else-if="doctors.length === 0" class="empty-container">
        <a-empty
          description="没有找到符合条件的医生"
        />
      </div>

      <!-- 医生列表 -->
      <div v-else class="doctors-grid">
        <a-card
          v-for="doctor in doctors"
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Doctor } from '../store';
import { doctorApi } from '../services/api';
import {
  UserOutlined,
  SearchOutlined
} from '@ant-design/icons-vue';

const router = useRouter();

// 状态管理
const doctors = ref<Doctor[]>([]);
const departments = ref<string[]>([]);
const titles = ref<string[]>([]);
const loading = ref(false);
const error = ref(false);

// 筛选条件
const filters = ref({
  name: '',
  department: '',
  title: '',
  isActive: ''
});

// 加载科室和职称数据
const loadOptions = async () => {
  try {
    const [deptData, titleData] = await Promise.all([
      doctorApi.getAllDepartments(),
      doctorApi.getAllTitles()
    ]);
    departments.value = deptData;
    titles.value = titleData;
  } catch (err) {
    console.error('Failed to load options:', err);
  }
};

// 加载医生数据
const loadDoctors = async () => {
  loading.value = true;
  error.value = false;
  
  try {
    const params = {
      name: filters.value.name || undefined,
      department: filters.value.department || undefined,
      title: filters.value.title || undefined,
      isActive: filters.value.isActive === '' ? undefined : filters.value.isActive === 'true'
    };
    
    const data = await doctorApi.getDoctorsByConditions(params);
    // 处理 specialties 字段，确保是数组格式
    doctors.value = data.map(doctor => ({
      ...doctor,
      specialties: Array.isArray(doctor.specialties) 
        ? doctor.specialties 
        : typeof doctor.specialties === 'string' 
          ? JSON.parse(doctor.specialties) 
          : []
    }));
  } catch (err) {
    console.error('Failed to load doctors:', err);
    error.value = true;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  loadDoctors();
};

// 重置筛选条件
const resetFilters = () => {
  filters.value = {
    name: '',
    department: '',
    title: '',
    isActive: ''
  };
  loadDoctors();
};

// 跳转到问诊页面
const goToConsultation = (doctor: Doctor) => {
  router.push(`/consultation/${doctor.username}`);
};

// 组件挂载时加载数据
onMounted(async () => {
  await loadOptions();
  await loadDoctors();
});
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

.filter-section {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 16px 0;
}

.filter-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-item {
  min-width: 150px;
  flex: 1;
  max-width: 200px;
}

.doctors-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 24px;
}

.loading-container,
.error-container,
.empty-container {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  padding: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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

  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-item {
    max-width: none;
  }

  .doctors-grid {
    grid-template-columns: 1fr;
  }
}
</style>
