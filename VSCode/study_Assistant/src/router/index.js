import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
    path: '/',
    redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: ()=>import('@/components/Login.vue')
    },
    {
    path: '/register',
    name: 'Register',
    component: ()=>import('@/components/Register.vue')
    },
    {
    path: '/student/home',
    name: 'StudentHome',
    component:()=>import('@/views/StudentHome.vue')
    },
  ],
})

export default router
