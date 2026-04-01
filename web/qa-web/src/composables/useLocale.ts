import { computed } from 'vue';
import { store } from '../store';
import { locales } from '../locales';

export const useLocale = () => {
  const currentLocale = computed(() => store.getCurrentLocale());
  
  const t = (key: string) => {
    const keys = key.split('.');
    let value: any = locales[currentLocale.value];
    
    for (const k of keys) {
      if (value && typeof value === 'object' && k in value) {
        value = value[k];
      } else {
        return key;
      }
    }
    
    return value || key;
  };

  return {
    t,
    currentLocale
  };
};