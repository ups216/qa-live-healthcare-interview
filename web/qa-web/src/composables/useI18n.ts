import { ref, computed } from 'vue'
import zhCN from '../locales/zh-CN.json'
import enUS from '../locales/en-US.json'

export type Locale = 'zh-CN' | 'en-US'

export interface LocaleMessages {
  app: {
    title: string
    home: string
    consultation: string
    doctors: string
    about: string
    doctorLogin: string
  }
  home: {
    heroTitle: string
    heroSubtitle: string
    feature1: string
    feature2: string
    feature3: string
    consultationBtn: string
    doctorsBtn: string
    statistics: {
      doctors: string
      questions: string
      activeSessions: string
      sessions: string
    }
    activeRooms: {
      title: string
      subtitle: string
      enterRoom: string
      online: string
    }
  }
}

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
} as const

const currentLocale = ref<Locale>('zh-CN')

const availableLocales = [
  { value: 'zh-CN', label: '中文' },
  { value: 'en-US', label: 'English' }
]

export function useI18n() {
  const locale = computed(() => currentLocale.value)
  
  const t = computed(() => messages[currentLocale.value])
  
  const setLocale = (newLocale: Locale) => {
    currentLocale.value = newLocale
  }
  
  return {
    locale,
    t,
    setLocale,
    availableLocales
  }
}