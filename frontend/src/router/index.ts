//import { createRouter, createWebHistory } from 'vue-router'

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes: [],
// })

// export default router
import { createRouter, createWebHistory } from 'vue-router'

// 直接使用相对路径 ../，完全不依赖 @ 别名
const Home = () => import('../views/Home.vue')
const DataRecord = () => import('../views/data/Record.vue')
const DataAnalysis = () => import('../views/data/Analysis.vue')
const HealthPlan = () => import('../views/Plan.vue')
const SettingProfile = () => import('../views/setting/Profile.vue')
const Login = () => import('../views/Login.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: Login },
    { path: '/', redirect: '/home' },
    { path: '/home', component: Home, meta: { title: '系统首页' } },
    {
      path: '/data',
      redirect: '/data/record',
      children: [
        { path: 'record', component: DataRecord, meta: { title: '数据记录' } },
        { path: 'analysis', component: DataAnalysis, meta: { title: '数据分析' } }
      ]
    },
    { path: '/plan', component: HealthPlan, meta: { title: '健康计划' } },
    {
      path: '/setting',
      redirect: '/setting/profile',
      children: [
        { path: 'profile', component: SettingProfile, meta: { title: '个人中心' } }
      ]
    }
  ]
})

export default router