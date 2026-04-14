// src/stores/user.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 全局用户信息
  const userInfo = ref({
    id: 0,
    username: '',
    nickname: '',
    avatar: '',
  })

  // 登录状态
  const isLogin = ref(false)

  // 登录方法：保存用户信息，标记为已登录
  const login = (data: any) => {
    userInfo.value = data
    isLogin.value = true
  }

  // 退出登录方法：清空信息，标记为未登录
  const logout = () => {
    userInfo.value = { id: 0, username: '', nickname: '', avatar: '' }
    isLogin.value = false
  }

  return { userInfo, isLogin, login, logout }
})