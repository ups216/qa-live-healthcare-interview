import { createI18n } from 'vue-i18n';
import zhCN from './zh-CN';
import en from './en';

const LOCALE_STORAGE_KEY = 'qa-web-locale';

const savedLocale = localStorage.getItem(LOCALE_STORAGE_KEY);
const defaultLocale = savedLocale === 'en' || savedLocale === 'zh-CN' ? savedLocale : 'zh-CN';

const i18n = createI18n({
  legacy: false,
  locale: defaultLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    en,
  },
});

export function setLocale(locale: 'zh-CN' | 'en') {
  i18n.global.locale.value = locale;
  localStorage.setItem(LOCALE_STORAGE_KEY, locale);
}

export default i18n;
