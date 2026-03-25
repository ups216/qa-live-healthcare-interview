import { createI18n } from 'vue-i18n';
import zh from './locales/zh.json';
import en from './locales/en.json';

const i18n = createI18n({
  locale: 'zh', // 默认语言
  fallbackLocale: 'en',
  messages: {
    zh,
    en
  }
});

export default i18n;