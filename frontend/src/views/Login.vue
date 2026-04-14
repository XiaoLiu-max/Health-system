<template>
  <div class="login-container">
    <el-card class="login-card">
      <h1 style="text-align: center; margin-bottom: 24px">健康管理系统登录</h1>
      <el-form :model="loginForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  // 这里后续可以对接后端登录接口，现在先模拟登录成功
  ElMessage.success('登录成功')
  router.push('/home')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}
.login-card {
  width: 400px;
}
</style>