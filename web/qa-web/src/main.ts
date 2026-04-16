import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './style.css';
import App from './App.vue';
import router from './router';
import { store } from './store';

const app = createApp(App);

app.use(Antd);
app.use(router);

await store.initializeSession();

app.mount('#app');
