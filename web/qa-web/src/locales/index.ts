import zhCN from './zh-CN.json';
import enUS from './en-US.json';

export type LocaleType = 'zh-CN' | 'en-US';

export interface LocaleMessages {
  common: {
    appName: string;
    home: string;
    consultation: string;
    doctors: string;
    about: string;
    doctorLogin: string;
    language: string;
    chinese: string;
    english: string;
    logout: string;
  };
  home: {
    title: string;
    subtitle: string;
    features: {
      team: string;
      realtime: string;
      privacy: string;
    };
    startConsultation: string;
    viewDoctors: string;
    statistics: {
      doctors: string;
      questions: string;
      activeSessions: string;
      sessions: string;
    };
    openRooms: string;
    roomsSubtitle: string;
    enterRoom: string;
    online: string;
  };
  doctor: {
    specialties: {
      general: string;
      pediatrics: string;
      internal: string;
      surgery: string;
      gynecology: string;
      emergency: string;
      "心内科": string;
      "骨科": string;
      "妇产科": string;
      "消化内科": string;
    };
  };
}

export const locales: Record<LocaleType, LocaleMessages> = {
  'zh-CN': zhCN,
  'en-US': enUS,
};

export const defaultLocale: LocaleType = 'zh-CN';

export function getLocale(): LocaleType {
  const saved = localStorage.getItem('locale');
  return (saved as LocaleType) || defaultLocale;
}

export function setLocale(locale: LocaleType): void {
  localStorage.setItem('locale', locale);
  // 触发语言变更事件，以便组件可以响应更新而不需要刷新页面
  window.dispatchEvent(new CustomEvent('localechange', { detail: { locale } }));
}

export function t(key: string, locale: LocaleType = getLocale()): string {
  const keys = key.split('.');
  let value: any = locales[locale];
  
  for (const k of keys) {
    if (value && typeof value === 'object' && k in value) {
      value = value[k];
    } else {
      console.warn(`Translation key "${key}" not found in locale "${locale}"`);
      return key;
    }
  }
  
  return value as string;
}

export function formatDoctorTitle(doctor: any, locale: LocaleType): string {
  if (!doctor.title || !doctor.department) return '';
  
  const title = doctor.title;
  const departmentKey = doctor.department;
  
  // 尝试获取翻译，如果找不到则使用原始部门名称
  const department = (locales[locale].doctor.specialties as Record<string, string>)[departmentKey] || doctor.department;
  
  return `${title} · ${department}`;
}