export { default as zhCN } from './zh-CN';
export { default as enUS } from './en-US';

export type Locale = 'zh-CN' | 'en-US';

export const localeNames: Record<Locale, string> = {
  'zh-CN': '中文',
  'en-US': 'English'
};
