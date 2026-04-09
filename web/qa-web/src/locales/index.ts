import zhCN from './zh-CN';
import enUS from './en-US';
import { ref, computed } from 'vue';

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
};

const currentLocale = ref('zh-CN');

function setLocale(locale: string) {
  currentLocale.value = locale;
}

function getLocale() {
  return currentLocale.value;
}

function t(key: string) {
  return computed(() => {
    const keys = key.split('.');
    let value: any = messages[currentLocale.value];
    
    for (const k of keys) {
      if (value === undefined) break;
      value = value[k];
    }
    
    return value || key;
  });
}

export {
  messages,
  setLocale,
  getLocale,
  t,
  currentLocale
};