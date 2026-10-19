import { ref } from 'vue';
import zh from './zh';
import en from './en';

export type Locale = 'zh' | 'en';

const messages: Record<Locale, Record<string, string>> = { zh, en };

const STORAGE_KEY = 'qa-locale';

const saved =
  typeof localStorage !== 'undefined'
    ? (localStorage.getItem(STORAGE_KEY) as Locale | null)
    : null;

const currentLocale = ref<Locale>(saved === 'zh' || saved === 'en' ? saved : 'zh');

export function useI18n() {
  const t = (key: string): string => {
    return messages[currentLocale.value][key] ?? key;
  };

  const setLocale = (locale: Locale) => {
    currentLocale.value = locale;
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(STORAGE_KEY, locale);
    }
  };

  const toggleLocale = () => {
    setLocale(currentLocale.value === 'zh' ? 'en' : 'zh');
  };

  return { t, currentLocale, setLocale, toggleLocale };
}

// 医生字段中英映射（数据本身仅含中文，按语言查表展示）
const doctorTranslations: Record<string, string> = {
  // 职称
  '主任医师': 'Chief Physician',
  '副主任医师': 'Associate Chief Physician',
  '主治医师': 'Attending Physician',
  // 科室
  '心内科': 'Cardiology',
  '儿科': 'Pediatrics',
  '骨科': 'Orthopedics',
  '妇产科': 'Obstetrics & Gynecology',
  '消化内科': 'Gastroenterology',
  // 专长
  '高血压': 'Hypertension',
  '冠心病': 'Coronary Heart Disease',
  '心律失常': 'Arrhythmia',
  '儿童感冒': 'Pediatric Cold',
  '儿童发育': 'Child Development',
  '疫苗接种': 'Vaccination',
  '骨折': 'Fractures',
  '关节炎': 'Arthritis',
  '运动损伤': 'Sports Injuries',
  '孕期保健': 'Prenatal Care',
  '妇科炎症': 'Gynecological Inflammation',
  '产后恢复': 'Postpartum Recovery',
  '胃炎': 'Gastritis',
  '肠道疾病': 'Intestinal Diseases',
  '肝病': 'Liver Diseases',
};

export function translateDoctorField(value: string): string {
  if (currentLocale.value === 'zh') return value;
  return doctorTranslations[value] ?? value;
}

export function translateExperience(value: string): string {
  if (currentLocale.value === 'zh') return value;
  const match = value.match(/^(\d+)年临床经验$/);
  if (match) {
    return `${match[1]} years of clinical experience`;
  }
  return value;
}

// 医生姓名音译（数据仅含中文名，手工维护）
const doctorNameTranslations: Record<string, string> = {
  '张伟医生': 'Dr. Zhang Wei',
  '李娜医生': 'Dr. Li Na',
  '王强医生': 'Dr. Wang Qiang',
  '刘敏医生': 'Dr. Liu Min',
  '陈杰医生': 'Dr. Chen Jie',
};

export function translateDoctorName(value: string): string {
  if (currentLocale.value === 'zh') return value;
  return doctorNameTranslations[value] ?? value;
}

export default messages;
