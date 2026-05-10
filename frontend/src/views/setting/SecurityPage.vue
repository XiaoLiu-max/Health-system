<template>
  <div class="security-page">
    <h2>🔒 账号安全</h2>

    <div class="card">
      <h3>修改密码</h3>
      <el-form :model="pwdForm" label-width="120px">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updatePwd">确认修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card danger">
      <h3>注销账号</h3>
      <p>账号一旦注销，无法恢复！</p>
      <el-button type="danger" @click="deleteAccount">确认注销账号</el-button>
    </div>

    <div class="card danger">
      <h3>退出登录</h3>
      <p>退出当前账号，下次需要重新登录</p>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
})

// 修改密码
const updatePwd = async () => {
  await axios.post('/user/updatePassword', pwdForm.value)
  ElMessage.success('密码修改成功')
}

// 注销账号
const deleteAccount = async () => {
  try {
    await ElMessageBox.confirm('确定注销账号？此操作不可恢复！', '警告', {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await axios.post('/user/deleteAccount')
    ElMessage.success('账号已注销')
    localStorage.removeItem('token')
    router.push('/login')
  } catch {
    // 用户取消操作
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定退出登录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })

    await axios.post('/user/logout')
    localStorage.removeItem('token')
    ElMessage.success('退出成功')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.security-page {
  padding: 20px 32px;
  background: #edf7f0; /* 浅绿色背景 */
  min-height: calc(100vh - 60px);
}

.card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.card.danger {
  border: 1px solid #f56c6c;
}
</style>
