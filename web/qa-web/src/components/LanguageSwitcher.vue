<template>
  <a-dropdown :trigger="['click']" placement="bottomRight" class="language-switcher">
    <a class="language-trigger" @click.prevent>
      <GlobalOutlined class="language-icon" />
      <span class="language-text">{{ t('common.language') }}</span>
      <DownOutlined class="language-arrow" />
    </a>
    <template #overlay>
      <a-menu>
        <a-menu-item 
          :class="{ active: currentLocale === 'zh-CN' }"
          @click="switchLanguage('zh-CN')"
        >
          <span class="menu-item-content">
            <span class="language-flag">🇨🇳</span>
            <span class="language-name">{{ t('common.chinese') }}</span>
          </span>
        </a-menu-item>
        <a-menu-item
          :class="{ active: currentLocale === 'en-US' }"
          @click="switchLanguage('en-US')"
        >
          <span class="menu-item-content">
            <span class="language-flag">🇺🇸</span>
            <span class="language-name">{{ t('common.english') }}</span>
          </span>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { GlobalOutlined, DownOutlined } from '@ant-design/icons-vue';
import { setLocale, type LocaleType } from '../locales';
import { useLocale } from '../hooks/useLocale';

const { currentLocale, t } = useLocale();

const switchLanguage = (locale: LocaleType) => {
  if (currentLocale.value !== locale) {
    setLocale(locale);
  }
};
</script>

<style scoped>
.language-switcher {
  margin-left: 16px;
}

.language-trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 6px;
  color: rgba(0, 0, 0, 0.85);
  transition: all 0.3s;
  cursor: pointer;
}

.language-trigger:hover {
  background: rgba(0, 0, 0, 0.06);
}

.language-icon {
  font-size: 16px;
}

.language-text {
  font-size: 14px;
  font-weight: 500;
}

.language-arrow {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.language-flag {
  font-size: 16px;
}

.language-name {
  font-size: 14px;
}

:deep(.ant-dropdown-menu-item) {
  padding: 8px 16px !important;
}

:deep(.ant-dropdown-menu-item:hover) {
  background-color: #f5f5f5 !important;
}

:deep(.ant-dropdown-menu-item.active) {
  background-color: #e6f7ff !important;
  color: #1890ff !important;
}

:deep(.ant-dropdown-menu-item.active .language-name) {
  color: #1890ff !important;
}
</style>