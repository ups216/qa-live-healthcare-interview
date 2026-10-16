<template>
  <!--
    右上角语言切换器（需求1）。
    选用 a-select 而非 a-dropdown：select 自带当前值回显，
    用户一眼就能看出当前语言，且不必自己维护选中态文案。
  -->
  <a-select
    v-model:value="currentLocale"
    :options="options"
    size="small"
    class="lang-switcher"
    @change="handleChange"
  >
    <template #suffixIcon>
      <GlobalOutlined />
    </template>
  </a-select>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { GlobalOutlined } from '@ant-design/icons-vue';
import { LOCALE_OPTIONS, setLocale, i18n, type AppLocale } from '../locales';

const options = LOCALE_OPTIONS;

// 初始化为 i18n 当前生效的语言（已由 index.ts 结合 localStorage/浏览器语言推导）
const currentLocale = ref<AppLocale>(i18n.global.locale.value as AppLocale);

function handleChange(value: AppLocale) {
  // 语言切换与持久化统一收在 locales/index.ts 的 setLocale 中，组件只负责触发
  setLocale(value);
}
</script>

<style scoped>
.lang-switcher {
  width: 112px;
  flex: none;
  height: 32px;
}

/* 窄屏下收窄宽度，避免与导航菜单、"医生登录"按钮挤成一团 */
@media (max-width: 768px) {
  .lang-switcher {
    width: 92px;
  }
}
</style>
