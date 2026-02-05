/**
 * 首页国际化入口
 * 导出语言包与类型，供 Home.vue 与语言切换使用
 */
import zhCN from './zh-CN';
import en from './en';

export type LocaleKey = 'zh-CN' | 'en';

export const messages: Record<LocaleKey, typeof zhCN> = {
  'zh-CN': zhCN,
  en,
};

/** 根据 key 路径取文案，如 t('home.hero.title') */
export function t(locale: LocaleKey, key: string): string {
  const keys = key.split('.');
  let value: unknown = messages[locale];
  for (const k of keys) {
    if (value != null && typeof value === 'object' && k in value) {
      value = (value as Record<string, unknown>)[k];
    } else {
      return key;
    }
  }
  return typeof value === 'string' ? value : key;
}

export { zhCN, en };
