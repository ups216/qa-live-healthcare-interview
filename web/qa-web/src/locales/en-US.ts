import type { zhCN } from './zh-CN';

/**
 * 英文语言包。
 *
 * 使用 `satisfies typeof zhCN` 而非直接 `: typeof zhCN`：
 * 前者既保证键结构必须与中文包一致（漏 key 会编译报错），
 * 又保留字面量类型，便于后续做 $t 的 key 自动补全。
 */
export const enUS = {
  common: {
    online: 'Online',
    enterRoom: 'Enter Room',
  },
  home: {
    hero: {
      title: 'Professional Online Medical Consultation Platform',
      subtitle:
        'Connecting qualified doctors with patients for convenient and efficient medical consultations',
      feature: {
        professional: 'Professional Medical Team',
        realtime: 'Real-time Consultation',
        privacy: 'Privacy & Security',
      },
      action: {
        consult: 'Consult Now',
        doctors: 'Browse Doctors',
      },
    },
    stat: {
      doctors: 'Doctors',
      questions: 'Total Questions',
      pending: 'Pending Questions',
      rooms: 'Active Rooms',
    },
    rooms: {
      title: 'Open Consultation Rooms',
      subtitle: 'The following doctors are currently available — feel free to consult',
    },
  },
} satisfies typeof zhCN;
