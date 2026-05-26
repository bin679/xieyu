import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'users', name: 'Users', component: () => import('@/views/UserManage.vue') },
      { path: 'labs', name: 'Labs', component: () => import('@/views/LabManage.vue') },
      { path: 'equipment', name: 'Equipment', component: () => import('@/views/EquipmentManage.vue') },
      { path: 'borrows', name: 'Borrows', component: () => import('@/views/BorrowManage.vue') },
      { path: 'repairs', name: 'Repairs', component: () => import('@/views/RepairManage.vue') },
      { path: 'statistics', name: 'Statistics', component: () => import('@/views/Statistics.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const user = sessionStorage.getItem('user')
  if (to.path !== '/login' && to.path !== '/register' && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
