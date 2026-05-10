import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'

import DataRecord from '../views/data/Record.vue'
import DataAnalysis from '../views/data/Analysis.vue'
import HealthPlan from '../views/Plan.vue'
import UserProfile from '../views/setting/Profile.vue'
import Message from '../views/Message.vue'
import FriendReport from '../views/FriendReport.vue'
// 新增导入
import SettingPage from '../views/setting/SettingPage.vue'
import SecurityPage from '../views/setting/SecurityPage.vue'
import About from '../views/setting/About.vue'

import AiChat from '../views/AiChat.vue'

import WeekReport from '../views/data/Week.vue'
import MonthReport from '../views/data/Month.vue'

import MyHealthData from '../views/data/MyHealthData.vue'

const routes = [
  // {
  //   path: '/',
  //   redirect: '/login',
  // },
  // {
  //   path: '/login',
  //   name: 'Login',
  //   component: Login,
  // },
  // {
  //   path: '/home',
  //   name: 'Home',
  //   component: Home,
  // },

  {
    path: '/',
    redirect: '/home', // 这里改成跳 /home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/home', // 主页正常
    name: 'Home',
    component: Home,
  },
  // ====================== 首页跳转的路由 ======================
  {
    path: '/data/record',
    component: DataRecord,
  },
  {
    path: '/data/analysis',
    component: DataAnalysis,
  },
  {
    path: '/plan',
    component: HealthPlan,
  },
  {
    path: '/setting',
    component: SettingPage,
  },
  {
    path: '/setting/profile',
    component: UserProfile,
  },
  {
    path: '/setting/about',
    component: About,
  },
  {
    path: '/setting/security',
    component: SecurityPage,
  },
  {
    path: '/aiChat',
    name: 'AiChat',
    component: AiChat,
  },
  {
    path: '/message',
    component: Message,
  },
  {
    path: '/friend-report',
    component: FriendReport,
  },

  // ====================== 周报 + 月报 路由 ======================
  {
    path: '/data/week',
    component: WeekReport,
  },
  {
    path: '/data/month',
    component: MonthReport,
  },

  // ====================== ✅ 新模块：我的健康数据 ======================
  {
    path: '/data/my',
    component: MyHealthData,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 登录拦截：没 token 自动跳回登录页
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
