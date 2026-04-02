import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './style.css';
import App from './App.vue';
import router from './router';
import zh from './locales/zh.json';
import en from './locales/en.json';

const i18n = createI18n({
  legacy: false,
  locale: 'zh',
  fallbackLocale: 'en',
  messages: {
    zh,
    en
  }
});

const app = createApp(App);

app.use(Antd);
app.use(router);
app.use(i18n);
app.mount('#app');
