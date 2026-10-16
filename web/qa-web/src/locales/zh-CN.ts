/**
 * 中文语言包。
 *
 * 本文件是 i18n 的"源真值"：en-US.ts 通过 satisfies 约束必须与本文件结构一致，
 * 因此新增 key 时若英文包漏配，TypeScript 会在编译期报错（npm run build 即 vue-tsc -b）。
 *
 * ─── 翻译范围说明 ─────────────────────────────────────────
 * 按 README「只需要处理首页本身，无需处理其他页面」的要求，
 * 本语言包**只包含首页 Home.vue 的文案**。
 * 导航菜单(AppHeader)、页脚(AppFooter)、其他页面均保持中文原文，不做国际化。
 *
 * 另需注意：医生/ Card 中的姓名、职称、科室、擅长领域等来自数据库，
 * 属于**业务数据**而非界面文案，不在国际化范围内。
 */
export const zhCN = {
  common: {
    online: '在线',
    enterRoom: '进入诊室',
  },
  home: {
    hero: {
      title: '专业在线医疗问诊平台',
      subtitle: '连接专业医生与患者，提供便捷、高效的医疗咨询服务',
      feature: {
        professional: '专业医生团队',
        realtime: '实时在线问诊',
        privacy: '隐私安全保护',
      },
      action: {
        consult: '立即问诊',
        doctors: '查看医生',
      },
    },
    stat: {
      doctors: '专业医生',
      questions: '问题总数',
      pending: '待响应问题',
      rooms: '在线诊室',
    },
    rooms: {
      title: '开放诊室',
      subtitle: '以下医生诊室正在开放，欢迎咨询',
    },
  },
};
