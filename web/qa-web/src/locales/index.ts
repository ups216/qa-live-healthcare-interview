import { computed, reactive } from 'vue';
import zhCN from './zh-CN.json';
import en from './en.json';

type Locale = 'zh-CN' | 'en';

const messages: Record<Locale, any> = {
  'zh-CN': zhCN,
  en,
};

const state = reactive({
  locale: 'zh-CN' as Locale,
});

export function setLocale(locale: Locale) {
  state.locale = locale;
}

function getMessage(path: string, locale: Locale) {
  const parts = path.split('.');
  let value: any = messages[locale];
  for (const key of parts) {
    if (value == null) return path;
    value = value[key];
  }
  return typeof value === 'string' ? value : path;
}

export function useI18n() {
  const currentLocale = computed(() => state.locale);

  const t = (path: string): string => {
    return getMessage(path, state.locale);
  };

  return {
    t,
    currentLocale,
    setLocale,
  };
}

