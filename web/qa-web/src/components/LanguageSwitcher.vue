<template>
  <a-dropdown :trigger="['click']" placement="bottomRight">
    <a-button type="text" class="language-switcher">
      <GlobalOutlined />
      {{ getLocaleText(store.getCurrentLocale()) }}
      <DownOutlined />
    </a-button>
    <template #overlay>
      <a-menu @click="handleMenuClick">
        <a-menu-item key="zh-CN">
          <span :class="{ 'active': store.getCurrentLocale() === 'zh-CN' }">中文</span>
        </a-menu-item>
        <a-menu-item key="en-US">
          <span :class="{ 'active': store.getCurrentLocale() === 'en-US' }">English</span>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { GlobalOutlined, DownOutlined } from '@ant-design/icons-vue';
import { store } from '../store';
import { getLocaleText } from '../locales';

const handleMenuClick = ({ key }: { key: string }) => {
  store.setLocale(key as any);
};
</script>

<style scoped>
.language-switcher {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  border: none;
  box-shadow: none;
}

.language-switcher:hover {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.04);
}

.active {
  color: #1890ff;
  font-weight: 500;
}
</style>