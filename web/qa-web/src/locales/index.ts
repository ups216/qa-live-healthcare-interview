import { createI18n } from 'vue-i18n';
import zhCN from './zh-CN';
import enUS from './en-US';

export type LocaleType = 'zh-CN' | 'en-US';

const savedLocale = localStorage.getItem('locale') as LocaleType | null;

export const i18n = createI18n({
  legacy: false,
  locale: savedLocale || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
});

export const setLocale = (locale: LocaleType) => {
  i18n.global.locale.value = locale;
  localStorage.setItem('locale', locale);
};

export const getLocale = (): LocaleType => {
  return i18n.global.locale.value as LocaleType;
};
