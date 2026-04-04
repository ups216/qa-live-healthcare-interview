import { createI18n } from 'vue-i18n';
import { messages, locale } from './locales';

const i18n = createI18n({
  legacy: false,
  locale: locale,
  fallbackLocale: 'zh-CN',
  messages
});

export default i18n;
