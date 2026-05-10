<template>
  <el-container style="height: 100vh; border: 1px solid #eee">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #2c3e50">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        background-color="#2c3e50"
        text-color="#fff"
        active-text-color="#ffd04b"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>系统首页</span>
        </el-menu-item>

        <!-- 消息中心：已删除角标，不再显示未读数字 -->
        <el-menu-item index="/message">
          <el-icon><ChatDotRound /></el-icon>
          <span>消息中心</span>
        </el-menu-item>

        <el-menu-item index="/aiChat">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 健康助手</span>
        </el-menu-item>

        <el-menu-item index="/setting">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header
        style="
          background-color: #fff;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0 20px;
          border-bottom: 1px solid #eee;
        "
      >
        <div style="display: flex; align-items: center; gap: 10px">
          <img
            src="@/assets/logo.png"
            alt="Logo"
            style="
              width: 36px;
              height: 36px;
              border-radius: 50%;
              background-color: #fff;
              object-fit: contain;
            "
          />
          <h1 style="margin: 0; font-size: 20px; color: #2c3e50">健康管理系统</h1>
        </div>

        <div class="header-right">
          <el-avatar
            :size="40"
            :src="userAvatar"
            style="cursor: pointer"
            @click="showUserInfo = !showUserInfo"
          />
          <el-popover
            v-model:visible="showUserInfo"
            placement="bottom-end"
            width="160"
            trigger="manual"
          >
            <div class="user-menu">
              <div class="user-item" @click="goToProfile">个人中心</div>
              <div class="user-item" @click="logout">退出登录</div>
            </div>
            <template #reference>
              <span></span>
            </template>
          </el-popover>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main style="background-color: #f5f7fa; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Setting, ChatDotRound } from '@element-plus/icons-vue'
import axios from 'axios'
import request from './utils/request'

const router = useRouter()
const activeMenu = ref('/home')
const showUserInfo = ref(false)
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

// 消息未读数（虽然保留了变量，但已经不渲染了，可删除）
const unreadCount = ref(0)
;(window as any).appUnread = unreadCount
let timer: ReturnType<typeof setInterval> | null = null

// 获取未读消息总数（虽然保留了函数，但角标已删除，可删除）
const getUnreadCount = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      unreadCount.value = 0
      if ((window as any).appUnread) {
        ;(window as any).appUnread.value = 0
      }
      return
    }
    const res = await request.get('/message/unread/count')
    unreadCount.value = res.data || 0
    if ((window as any).appUnread) {
      ;(window as any).appUnread.value = res.data || 0
    }
  } catch (err) {
    console.log('获取未读消息失败', err)
    unreadCount.value = 0
    if ((window as any).appUnread) {
      ;(window as any).appUnread.value = 0
    }
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token && router.currentRoute.value.path !== '/login') {
    getUnreadCount()
    timer = setInterval(getUnreadCount, 5000)
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  router.push(index)
}

const goToProfile = () => {
  showUserInfo.value = false
  router.push('/setting/profile')
}

const logout = async () => {
  showUserInfo.value = false
  try {
    await axios.post('/user/logout')
  } catch (err) {}
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  height: 60px !important;
  line-height: 60px;
}
.el-aside {
  transition: width 0.3s;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-menu {
  padding: 8px 0;
}
.user-item {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.user-item:hover {
  background-color: #f5f7fa;
}
</style>
