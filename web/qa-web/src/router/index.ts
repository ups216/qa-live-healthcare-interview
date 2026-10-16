import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { store } from '../store';
import Home from '../views/Home.vue';
import Consultation from '../views/Consultation.vue';
import DoctorLogin from '../views/DoctorLogin.vue';
import DoctorRoom from '../views/DoctorRoom.vue';
import Doctors from '../views/Doctors.vue';
import About from '../views/About.vue';
import PatientLogin from '../views/PatientLogin.vue';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/consultation',
    name: 'Consultation',
    component: Consultation,
    meta: { requiresAuth: true },
  },
  {
    path: '/consultation/:doctorUsername',
    name: 'ConsultationRoom',
    component: Consultation,
    meta: { requiresAuth: true },
  },
  {
    path: '/doctors',
    name: 'Doctors',
    component: Doctors,
  },
  {
    path: '/about',
    name: 'About',
    component: About,
  },
  {
    path: '/doctor/login',
    name: 'DoctorLogin',
    component: DoctorLogin,
  },
  {
    path: '/doctor/room/:username',
    name: 'DoctorRoom',
    component: DoctorRoom,
  },
  {
    path: '/patient/login',
    name: 'PatientLogin',
    component: PatientLogin,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

/**
 * 路由守卫（需求3）。
 *
 * 仅对标记了 requiresAuth 的路由（问诊相关）做登录校验，
 * 首页、医生列表、关于等公开页面保持免登录可访问。
 * 未登录时带上 redirect 参数，登录后回到原本想去的页面。
 *
 * 说明：这里先尝试 restorePatient() 从服务端重建会话，
 * 目的是覆盖"刷新浏览器"场景 —— 旧实现把登录态放在内存，刷新即丢失。
 */
router.beforeEach(async (to) => {
  if (!to.meta.requiresAuth) {
    return true;
  }

  if (store.state.currentPatient) {
    return true;
  }

  const restored = await store.restorePatient();
  if (restored) {
    return true;
  }

  return {
    name: 'PatientLogin',
    query: { redirect: to.fullPath },
  };
});

export default router;
