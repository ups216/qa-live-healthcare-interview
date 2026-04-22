import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Home from '../views/Home.vue';
import Consultation from '../views/Consultation.vue';
import DoctorLogin from '../views/DoctorLogin.vue';
import DoctorRoom from '../views/DoctorRoom.vue';
import PatientLogin from '../views/PatientLogin.vue';
import PatientRoom from '../views/PatientRoom.vue';
import PatientResetPassword from '../views/PatientResetPassword.vue';
import Doctors from '../views/Doctors.vue';
import About from '../views/About.vue';

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
  },
  {
    path: '/consultation/:doctorUsername',
    name: 'ConsultationRoom',
    component: Consultation,
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
  {
    path: '/patient/room/:name',
    name: 'PatientRoom',
    component: PatientRoom,
  },
  {
    path: '/patient/reset-password',
    name: 'PatientResetPassword',
    component: PatientResetPassword,
  },
];


const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
