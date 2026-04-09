import { ref, computed } from 'vue';
import zhCN from './zh-CN';
import enUS from './en-US';

export type Locale = 'zh-CN' | 'en-US';

const messages: Record<Locale, typeof zhCN> = {
  'zh-CN': zhCN,
  'en-US': enUS,
};

// Global reactive locale state (shared across components)
const currentLocale = ref<Locale>('zh-CN');

export function useLocale() {
  const setLocale = (locale: Locale) => {
    currentLocale.value = locale;
  };

  const t = computed(() => messages[currentLocale.value]);

  return {
    currentLocale,
    setLocale,
    t,
  };
}
