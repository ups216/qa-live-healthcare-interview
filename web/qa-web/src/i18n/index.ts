import { createI18n } from 'vue-i18n';
import zhCN from '../locales/zh-CN.json';
import enUS from '../locales/en-US.json';

// 从 localStorage 获取保存的语言设置，默认中文
const getStoredLanguage = (): string => {
  if (typeof window !== 'undefined') {
    const stored = localStorage.getItem('home-language');
    if (stored && (stored === 'zh-CN' || stored === 'en-US')) {
      return stored;
    }
  }
  return 'zh-CN';
};

// 创建 i18n 实例
const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: getStoredLanguage(), // 默认语言
  fallbackLocale: 'zh-CN', // 回退语言
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
  // 添加全局警告处理器，避免缺失翻译时的控制台警告
  missing: (locale: string, key: string) => {
    console.warn(`[i18n] Missing translation: ${key} for locale: ${locale}`);
    return key;
  },
});

// 切换语言函数
export const setLanguage = (locale: string): void => {
  if (locale === 'zh-CN' || locale === 'en-US') {
    i18n.global.locale.value = locale;
    if (typeof window !== 'undefined') {
      localStorage.setItem('home-language', locale);
    }
    // 更新 HTML lang 属性
    document.documentElement.lang = locale === 'zh-CN' ? 'zh-CN' : 'en';
  }
};

// 获取当前语言
export const getCurrentLanguage = (): string => {
  return i18n.global.locale.value;
};

export default i18n;
