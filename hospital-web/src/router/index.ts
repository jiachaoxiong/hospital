import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 路由配置，含角色守卫
const routes: RouteRecordRaw[] = [
  {
    path: '/patient',
    component: () => import('@/layouts/PatientLayout.vue'),
    meta: { role: 'PATIENT' },
    children: [
      { path: 'hospitals', component: () => import('@/views/patient/Hospitals.vue') },
      { path: 'hospital/:id', component: () => import('@/views/patient/HospitalDetail.vue') },
      { path: 'booking/:scheduleId', component: () => import('@/views/patient/BookingConfirm.vue') },
      { path: 'orders', component: () => import('@/views/patient/MyOrders.vue') },
      { path: 'order-detail/:id', component: () => import('@/views/patient/OrderDetail.vue') },
    ],
  },
  {
    path: '/doctor',
    component: () => import('@/layouts/DoctorLayout.vue'),
    meta: { role: 'DOCTOR' },
    children: [
      { path: 'schedule', component: () => import('@/views/doctor/DoctorSchedule.vue') },
      { path: 'patients', component: () => import('@/views/doctor/DoctorPatients.vue') },
    ],
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: 'hospitals', component: () => import('@/views/admin/AdminHospitals.vue') },
      { path: 'doctors', component: () => import('@/views/admin/AdminDoctors.vue') },
      { path: 'schedules', component: () => import('@/views/admin/AdminSchedules.vue') },
      { path: 'orders', component: () => import('@/views/admin/AdminOrders.vue') },
    ],
  },
  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/register', component: () => import('@/views/Register.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/patient/hospitals' },
];

const router = createRouter({ history: createWebHistory(), routes });

// 路由守卫：检查 token 和角色
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken');
  const role = localStorage.getItem('role');
  if (to.path === '/login' || to.path === '/register') {
    next();
  } else if (!token) {
    next('/login');
  } else if (to.meta.role && to.meta.role !== role) {
    if (role === 'PATIENT') next('/patient/hospitals');
    else if (role === 'DOCTOR') next('/doctor/schedule');
    else if (role === 'ADMIN') next('/admin/hospitals');
    else next('/login');
  } else {
    next();
  }
});

export default router;
