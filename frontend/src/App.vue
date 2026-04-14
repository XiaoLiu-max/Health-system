<!-- <script setup lang="ts"></script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>
</template>

<style scoped></style> -->
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
        <el-sub-menu index="/data">
          <template #title>
            <el-icon><DataLine /></el-icon>
            <span>健康数据</span>
          </template>
          <el-menu-item index="/data/record">数据记录</el-menu-item>
          <el-menu-item index="/data/analysis">数据分析</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/plan">
          <el-icon><Calendar /></el-icon>
          <span>健康计划</span>
        </el-menu-item>
        <el-menu-item index="/setting">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航栏 -->
      <el-header style="background-color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; border-bottom: 1px solid #eee">
        <h1 style="margin: 0; font-size: 20px; color: #2c3e50">健康管理系统</h1>
        <div class="header-right">
          <el-avatar :size="40" :src="userAvatar" style="cursor: pointer" @click="showUserInfo = !showUserInfo" />
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

      <!-- 主内容区 -->
      <el-main style="background-color: #f5f7fa; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, DataLine, Calendar, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('/home')
const showUserInfo = ref(false)
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  router.push(index)
}

const goToProfile = () => {
  showUserInfo.value = false
  router.push('/setting/profile')
}

const logout = () => {
  showUserInfo.value = false
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