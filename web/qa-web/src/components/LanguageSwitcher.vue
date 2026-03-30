<template>
  <a-dropdown placement="bottomRight" trigger="click">
    <a-button class="lang-switcher-btn">
      <GlobalOutlined style="margin-right: 4px;" />
      {{ language === 'zh' ? '中文' : 'EN' }}
    </a-button>
    <template #overlay>
      <a-menu @click="handleMenuClick">
        <a-menu-item key="zh" :disabled="language === 'zh'">
          🇨 中文
        </a-menu-item>
        <a-menu-item key="en" :disabled="language === 'en'">
          🇺 English
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { GlobalOutlined } from '@ant-design/icons-vue';
import { useLanguage } from '../composables/useLanguage';

const { language, setLanguage } = useLanguage();

const handleMenuClick = ({ key }: { key: string }) => {
  if (key === 'zh' || key === 'en') {
    setLanguage(key as 'zh' | 'en');
  }
};
</script>

<style scoped>
.lang-switcher-btn {
  border: 1px solid #d9d9d9;
}

.lang-switcher-btn:hover {
  border-color: #40a9ff;
  color: #40a9ff;
}
</style>
