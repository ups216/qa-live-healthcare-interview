import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './style.css';
import App from './App.vue';
import router from './router';
import i18n from './locales';

const app = createApp(App);

// i18n 须在 Antd 之前注册：Antd 组件内部有自己的 locale 体系，
// 先装 i18n 可以保证我们的总线先就位，避免组件内嵌文案的本地化行为不确定。
app.use(i18n);
app.use(Antd);
app.use(router);
app.mount('#app');
