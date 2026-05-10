<template>
  <div class="setting-page">
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="header-content">
        <el-avatar :size="90" :src="userAvatar" class="avatar" />
        <div class="user-info">
          <h2 class="username">{{ userInfo.username }}</h2>
          <p class="user-id">用户ID：{{ userInfo.id }}</p>
        </div>
      </div>
    </div>

    <div class="card-list">
      <div class="card-item" @click="goProfile">
        <div class="card-left">
          <div class="icon">👤</div>
          <div class="text">
            <div class="title">个人资料</div>
            <div class="desc">修改昵称、手机号、性别、年龄</div>
          </div>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>

      <div class="card-item" @click="goSecurity">
        <div class="card-left">
          <div class="icon">🔒</div>
          <div class="text">
            <div class="title">账号安全</div>
            <div class="desc">修改密码、注销账号、退出登录</div>
          </div>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>

      <div class="card-item" @click="goAbout">
        <div class="card-left">
          <div class="icon">ℹ️</div>
          <div class="text">
            <div class="title">关于我们</div>
            <div class="desc">健康管理系统使用说明与介绍</div>
          </div>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const userInfo = ref({ id: '', username: '' })
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

const loadUserInfo = async () => {
  try {
    const res = await axios.get('/user/info')
    userInfo.value = res.data.data
  } catch (err) {
    ElMessage.error('加载失败')
  }
}

const goProfile = () => router.push('/setting/profile')
const goSecurity = () => router.push('/setting/security')
const goAbout = () => router.push('/setting/about')

onMounted(() => loadUserInfo())
</script>

<style scoped>
.setting-page {
  width: 100%;
  padding: 0;
  margin: 0;
  background: #edf7f0; /* 浅绿色背景 */
  min-height: calc(100vh - 60px);
}

.profile-header {
  position: relative;
  width: 100%;
  background: #fff;
  overflow: hidden;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border-radius: 0;
}

.header-bg {
  height: 180px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 0 32px 32px;
  margin-top: -60px;
}

.avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.user-info .username {
  margin: 0;
  font-size: 26px;
  font-weight: 600;
  color: #222;
}

.user-info .user-id {
  margin: 4px 0 0;
  color: #666;
  font-size: 15px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  padding: 0 32px;
  box-sizing: border-box;
}

.card-item {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: 0.25s;
}

.card-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.card-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon {
  width: 46px;
  height: 46px;
  border-radius: 10px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.text .title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.text .desc {
  font-size: 13px;
  color: #999;
}
</style>
