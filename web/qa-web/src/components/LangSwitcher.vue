<template>
  <a-select
    v-model:value="currentLocale"
    :options="localeOptions"
    class="lang-select"
    @change="handleLocaleChange"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { setLocale } from '../locales';

const { locale } = useI18n();

const currentLocale = computed({
  get: () => locale.value as 'zh-CN' | 'en',
  set: (value: 'zh-CN' | 'en') => {
    locale.value = value;
  },
});

const localeOptions = [
  { value: 'zh-CN', label: '中文' },
  { value: 'en', label: 'English' },
];

const handleLocaleChange = (value: 'zh-CN' | 'en') => {
  setLocale(value);
};
</script>

<style scoped>
.lang-select {
  width: 110px;
}
</style>
