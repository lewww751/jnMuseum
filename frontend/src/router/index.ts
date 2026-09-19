import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // Public routes
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/public/Home.vue')
    },
    {
      path: '/exhibitions',
      name: 'Exhibitions',
      component: () => import('@/views/public/Exhibitions.vue')
    },
    {
      path: '/exhibitions/:id',
      name: 'ExhibitionDetail',
      component: () => import('@/views/public/ExhibitionDetail.vue')
    },
    {
      path: '/events',
      name: 'Events',
      component: () => import('@/views/public/Events.vue')
    },
    {
      path: '/collections',
      name: 'Collections',
      component: () => import('@/views/public/Collections.vue')
    },
    {
      path: '/visit',
      name: 'Visit',
      component: () => import('@/views/public/Visit.vue')
    },
    {
      path: '/about',
      name: 'About',
      component: () => import('@/views/public/About.vue')
    },
    {
      path: '/reserve',
      name: 'Reserve',
      component: () => import('@/views/public/Reserve.vue')
    },
    {
      path: '/my-booking',
      name: 'MyBooking',
      component: () => import('@/views/public/MyBooking.vue')
    },
    // Admin routes
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: () => import('@/views/admin/Login.vue')
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue')
        },
        {
          path: 'exhibitions',
          name: 'AdminExhibitions',
          component: () => import('@/views/admin/Exhibitions.vue')
        },
        {
          path: 'events',
          name: 'AdminEvents',
          component: () => import('@/views/admin/Events.vue')
        },
        {
          path: 'collections',
          name: 'AdminCollections',
          component: () => import('@/views/admin/Collections.vue')
        },
        {
          path: 'guide',
          name: 'AdminGuide',
          component: () => import('@/views/admin/Guide.vue')
        },
        {
          path: 'calendar',
          name: 'AdminCalendar',
          component: () => import('@/views/admin/Calendar.vue')
        },
        {
          path: 'bookings',
          name: 'AdminBookings',
          component: () => import('@/views/admin/Bookings.vue')
        }
      ]
    }
  ]
})

// Auth guard
router.beforeEach((to, from, next) => {
  const adminStore = useAdminStore()

  if (to.meta.requiresAuth && !adminStore.isAuthenticated) {
    next('/admin/login')
  } else if (to.path === '/admin/login' && adminStore.isAuthenticated) {
    next('/admin')
  } else {
    next()
  }
})

export default router