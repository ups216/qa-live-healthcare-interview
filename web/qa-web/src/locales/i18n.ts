import { computed, readonly, ref } from 'vue';
import zhCN from './zh-CN';
import enUS from './en-US';

export type Locale = 'zh-CN' | 'en-US';

const STORAGE_KEY = 'qa-web-locale';

const messages: Record<Locale, Record<string, unknown>> = {
  'zh-CN': zhCN,
  'en-US': enUS
};

function loadInitialLocale(): Locale {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved === 'zh-CN' || saved === 'en-US') return saved;
  return 'zh-CN';
}

const localeRef = ref<Locale>(loadInitialLocale());

function setLocale(next: Locale) {
  localeRef.value = next;
  localStorage.setItem(STORAGE_KEY, next);
}

function getByPath(obj: unknown, path: string): unknown {
  return path.split('.').reduce<unknown>((acc, key) => {
    if (acc && typeof acc === 'object' && key in (acc as Record<string, unknown>)) {
      return (acc as Record<string, unknown>)[key];
    }
    return undefined;
  }, obj);
}

function t(key: string): string {
  const msg = getByPath(messages[localeRef.value], key);
  return typeof msg === 'string' ? msg : key;
}

export function useI18n() {
  return {
    locale: readonly(localeRef),
    localeLabel: computed(() => (localeRef.value === 'zh-CN' ? '中文' : 'English')),
    setLocale,
    t
  };
}

