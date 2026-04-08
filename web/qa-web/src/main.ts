import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './style.css';
import App from './App.vue';
import router from './router';
import zhCN from './locales/zh-CN.json';
import enUS from './locales/en-US.json';

const app = createApp(App);

// 创建 i18n 实例
const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: 'zh-CN', // 默认语言
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
})

app.use(Antd);
app.use(router);
app.use(i18n);
app.mount('#app');
