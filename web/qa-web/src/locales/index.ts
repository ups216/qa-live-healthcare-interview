import { ref, computed } from 'vue'
import zhCN from './zh-CN'
import enUS from './en-US'

const locale = ref<'zh-CN' | 'en-US'>('zh-CN')

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
}

export function useLocale() {
  const current = computed(() => messages[locale.value])

  const setLocale = (value: 'zh-CN' | 'en-US') => {
    locale.value = value
  }

  return {
    locale,
    current,
    setLocale,
  }
}