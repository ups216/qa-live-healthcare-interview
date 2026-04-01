import zhCN from './zh-CN';
import enUS from './en-US';

export type Locale = 'zh-CN' | 'en-US';

export const locales = {
  'zh-CN': zhCN,
  
  'en-US': enUS
};

export const defaultLocale: Locale = 'zh-CN';

export const getLocaleText = (locale: Locale) => {
  const localeMap = {
    'zh-CN': '中文',
    'en-US': 'English'
  };
  return localeMap[locale];
};