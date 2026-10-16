import { createI18n } from 'vue-i18n';
import { zhCN } from './zh-CN';
import { enUS } from './en-US';

/**
 * 语言持久化 key。与需求3 的 token key 分开命名，互不干扰。
 */
const LOCALE_STORAGE_KEY = 'qa-live-locale';

export type AppLocale = 'zh-CN' | 'en-US';

export const LOCALE_OPTIONS: { value: AppLocale; label: string }[] = [
  // label 刻意不做翻译：语言名永远以该语言自身的写法展示，这是 i18n 的通用惯例
  { value: 'zh-CN', label: '中文' },
  { value: 'en-US', label: 'English' },
];

/**
 * 依据浏览器语言推断初始语言。
 * 命中 zh* 用中文，命中 en* 用英文，其余回落中文（系统默认语言）。
 */
function detectBrowserLocale(): AppLocale {
  const nav = typeof navigator !== 'undefined' ? navigator.language : '';
  if (nav.startsWith('en')) {
    return 'en-US';
  }
  if (nav.startsWith('zh')) {
    return 'zh-CN';
  }
  return 'zh-CN';
}

/**
 * 决定初始语言，优先级：
 * 1) localStorage 中用户上一次的显式选择
 * 2) 浏览器语言
 * 3) 回落 zh-CN
 */
function resolveInitialLocale(): AppLocale {
  try {
    const saved = localStorage.getItem(LOCALE_STORAGE_KEY);
    if (saved === 'zh-CN' || saved === 'en-US') {
      return saved;
    }
  } catch {
    // 隐私模式下 localStorage 可能不可用，忽略并继续走浏览器推断
  }
  return detectBrowserLocale();
}

function persistLocale(locale: AppLocale): void {
  try {
    localStorage.setItem(LOCALE_STORAGE_KEY, locale);
  } catch {
    // 忽略写入失败，仅影响"下次保持语言"，不影响本次会话切换
  }
}

export const i18n = createI18n({
  // legacy: false 是使用 Composition API（useI18n）的前提
  legacy: false,
  locale: resolveInitialLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
});

/**
 * 切换语言：同步 i18n.locale、持久化、并更新 <html lang>。
 *
 * 更新 <html lang> 的两点意义：
 * 1) 屏幕阅读器据此选择发音引擎，影响无障碍体验；
 * 2) 顺带修正 index.html 原本写死 lang="en" 的问题。
 */
export function setLocale(locale: AppLocale): void {
  i18n.global.locale.value = locale;
  persistLocale(locale);
  if (typeof document !== 'undefined') {
    document.documentElement.lang = locale;
  }
}

/** 初始化时同步一次 <html lang>，保证首帧即与实际语言一致 */
if (typeof document !== 'undefined') {
  document.documentElement.lang = i18n.global.locale.value as AppLocale;
}

export default i18n;
