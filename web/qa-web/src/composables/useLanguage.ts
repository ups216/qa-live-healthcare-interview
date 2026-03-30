import { ref, computed } from 'vue';
import zh from '../locales/zh.json';
import en from '../locales/en.json';

type Language = 'zh' | 'en';

// 获取初始语言
const getInitialLanguage = (): Language => {
  if (typeof localStorage !== 'undefined') {
    const saved = localStorage.getItem('language');
    if (saved === 'zh' || saved === 'en') {
      return saved;
    }
  }
  return 'zh';
};

// 全局单例状态 - 使用 ref 保证响应式
const currentLanguage = ref<Language>(getInitialLanguage());

// 计算属性返回当前语言的翻译
const translations = computed(() => 
  currentLanguage.value === 'zh' ? zh : en
);

export function useLanguage() {
  const setLanguage = (lang: Language) => {
    currentLanguage.value = lang;
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('language', lang);
    }
  };

  return {
    language: currentLanguage,
    t: translations,
    setLanguage
  };
}
