import { ref, onMounted, onUnmounted } from 'vue';
import { t as translate, getLocale, type LocaleType } from '../locales';

export function useLocale() {
  const currentLocale = ref<LocaleType>(getLocale());
  
  const handleLocaleChange = (event: CustomEvent) => {
    if (event.detail && event.detail.locale) {
      currentLocale.value = event.detail.locale;
    }
  };
  
  // 响应式的翻译函数
  const t = (key: string) => {
    return translate(key, currentLocale.value);
  };
  
  onMounted(() => {
    window.addEventListener('localechange', handleLocaleChange as EventListener);
  });
  
  onUnmounted(() => {
    window.removeEventListener('localechange', handleLocaleChange as EventListener);
  });
  
  return {
    currentLocale,
    t,
  };
}