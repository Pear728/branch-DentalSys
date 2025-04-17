import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  // 患者端路由
  {
    path: '/patient',
    component: () => import('../views/patient/Layout.vue'),
    children: [
      {
        path: '',
        redirect: 'home'
      },
      {
        path: 'home',
        name: 'PatientHome',
        component: () => import('../views/patient/Home.vue')
      },
      {
        path: 'appointment',
        name: 'PatientAppointment',
        component: () => import('../views/patient/Appointment.vue')
      },
      {
        path: 'records',
        name: 'PatientRecords',
        component: () => import('../views/patient/MedicalRecords.vue')
      },
      {
        path: 'payment',
        name: 'PatientPayment',
        component: () => import('../views/patient/Payment.vue')
      },
      {
        path: 'profile',
        name: 'PatientProfile',
        component: () => import('../views/patient/Profile.vue')
      }
    ]
  },
  // 医生端路由
  {
    path: '/doctor',
    component: () => import('../views/doctor/Layout.vue'),
    children: [
      {
        path: '',
        redirect: 'home'
      },
      {
        path: 'home',
        name: 'DoctorHome',
        component: () => import('../views/doctor/Home.vue')
      },
      {
        path: 'appointments',
        name: 'DoctorAppointments',
        component: () => import('../views/doctor/Appointments.vue')
      },
      {
        path: 'records',
        name: 'DoctorMedicalRecords',
        component: () => import('../views/doctor/MedicalRecords.vue')
      },
      {
        path: 'schedule',
        name: 'DoctorSchedule',
        component: () => import('../views/doctor/Schedule.vue')
      },
      {
        path: 'profile',
        name: 'DoctorProfile',
        component: () => import('../views/doctor/Profile.vue')
      }
    ]
  },
  // 管理员端路由
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    children: [
      {
        path: '',
        redirect: 'home'
      },
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('../views/admin/Home.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      },
      {
        path: 'data',
        name: 'AdminData',
        component: () => import('../views/admin/Data.vue')
      }
    ]
  },
  {
    path: '*',
    redirect: '/'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫，检查登录状态
router.beforeEach((to, from, next) => {
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)
  const user = JSON.parse(localStorage.getItem('user'))

  // 需要登录但用户未登录时，重定向到登录页
  if (authRequired && !user) {
    next('/login')
  } else {
    // 根据用户角色限制访问
    if (user) {
      // 处理不同类型的角色值（数字或字符串）
      const role = user.role;
      const roleValue = parseInt(role) || role;
      
      // 判断是否为患者角色
      const isPatient = roleValue === 1 || roleValue === '1' || roleValue === 'PATIENT';
      // 判断是否为医生角色
      const isDoctor = roleValue === 2 || roleValue === '2' || roleValue === 'DOCTOR';
      // 判断是否为管理员角色
      const isAdmin = roleValue === 3 || roleValue === '3' || roleValue === 'ADMIN';
      
      // 根据路径和角色进行跳转
      if (to.path.startsWith('/patient') && !isPatient) {
        next('/login')
      } else if (to.path.startsWith('/doctor') && !isDoctor) {
        next('/login')
      } else if (to.path.startsWith('/admin') && !isAdmin) {
        next('/login')
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

export default router
