import zhCN from './zh-CN';
import enUS from './en-US';

interface Locale {
  [key: string]: any;
}

const locales: Record<string, Locale> = {
  'zh-CN': zhCN,
  'en-US': enUS
};

// 从本地存储中获取语言设置，默认为'zh-CN'
let currentLocale = localStorage.getItem('locale') || 'zh-CN';

export function setLocale(locale: string) {
  if (locales[locale]) {
    currentLocale = locale;
    // 将语言设置存储到本地存储
    localStorage.setItem('locale', locale);
  }
}

export function getLocale() {
  return currentLocale;
}

export function t(key: string): string {
  let result: any = locales[currentLocale];
  let currentKey = key;
  
  // 处理包含数组索引的情况，如 'home.features.items[0]'
  const parts = currentKey.split('.');
  
  for (const part of parts) {
    // 检查是否包含数组索引
    const arrayMatch = part.match(/^(\w+)\[(\d+)\]$/);
    if (arrayMatch) {
      const [, arrayName, index] = arrayMatch;
      if (result && typeof result === 'object' && arrayName in result) {
        const array = result[arrayName];
        if (Array.isArray(array) && parseInt(index) < array.length) {
          result = array[parseInt(index)];
        } else {
          return key;
        }
      } else {
        return key;
      }
    } else {
      // 普通对象属性
      if (result && typeof result === 'object' && part in result) {
        result = result[part];
      } else {
        return key;
      }
    }
  }
  
  return result;
}

export default {
  setLocale,
  getLocale,
  t
};