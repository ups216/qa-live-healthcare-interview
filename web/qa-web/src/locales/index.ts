/**
 * 国际化：语言资源与当前语言状态。
 * 仅首页使用，语言资源文件存放在 src/locales 目录（需求 AC1.6）。
 */
import { ref, computed } from 'vue';
import zhCN from './zh-CN.json';
import en from './en.json';

export type LocaleKey = 'zh-CN' | 'en';

const messages: Record<LocaleKey, Record<string, unknown>> = {
  'zh-CN': zhCN as Record<string, unknown>,
  en: en as Record<string, unknown>,
};

// 全局当前语言，持久化到 localStorage 以便刷新后保持
const currentLocale = ref<LocaleKey>((localStorage.getItem('locale') as LocaleKey) || 'zh-CN');

export function useLocale() {
  const locale = computed(() => currentLocale.value);
  const setLocale = (key: LocaleKey) => {
    currentLocale.value = key;
    localStorage.setItem('locale', key);
  };
  const t = (key: string): string => {
    const parts = key.split('.');
    let value: unknown = messages[currentLocale.value];
    for (const p of parts) {
      value = (value as Record<string, unknown>)?.[p];
    }
    return typeof value === 'string' ? value : key;
  };
  return { locale, setLocale, t };
}

export function getCurrentLocale(): LocaleKey {
  return currentLocale.value;
}

export function setCurrentLocale(key: LocaleKey) {
  currentLocale.value = key;
  localStorage.setItem('locale', key);
}
