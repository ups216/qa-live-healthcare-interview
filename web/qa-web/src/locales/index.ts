import zhCN from './zh-CN.json';
import enUS from './en-US.json';

export { zhCN, enUS };

export type Locale = 'zh-CN' | 'en-US';

export const messages: Record<Locale, typeof zhCN> = {
  'zh-CN': zhCN,
  'en-US': enUS
};

export const localeNames: Record<Locale, string> = {
  'zh-CN': '中文',
  'en-US': 'English'
};

export const locale: Locale = 'zh-CN';
