import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './style.css';
import App from './App.vue';
import router from './router';
import { store } from './store';

async function bootstrap() {
  await store.initPatientSession();
  const app = createApp(App);
  app.use(Antd);
  app.use(router);
  app.mount('#app');
}

bootstrap();
